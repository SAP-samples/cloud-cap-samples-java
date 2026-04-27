package my.bookshop.handlers;

import static cds.gen.notesservice.NotesService_.ADDRESSES;
import static cds.gen.notesservice.NotesService_.NOTES;

import cds.gen.api_business_partner.ApiBusinessPartner;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.notesservice.Addresses;
import cds.gen.notesservice.Addresses_;
import cds.gen.notesservice.Notes;
import cds.gen.notesservice.NotesService_;
import cds.gen.notesservice.Notes_;
import com.sap.cds.CdsResult;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnExpand;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnReference.Segment;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.ql.cqn.CqnStructuredTypeRef;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@ServiceName(NotesService_.CDS_NAME)
public class NotesServiceHandler implements EventHandler {

  private final ApiBusinessPartner bupa;
  private final CqnAnalyzer analyzer;

  NotesServiceHandler(
      @Qualifier(ApiBusinessPartner_.CDS_NAME) ApiBusinessPartner bupa, CdsModel model) {
    this.bupa = bupa;
    this.analyzer = CqnAnalyzer.create(model);
  }

  @On(entity = Addresses_.CDS_NAME)
  void readAddresses(CdsReadEventContext context) {
    List<? extends Segment> segments = context.getCqn().ref().segments();
    // via note
    if (segments.size() == 2 && segments.getFirst().id().equals(Notes_.CDS_NAME)) {
      Map<String, Object> noteKeys = analyzer.analyze(context.getCqn()).rootKeys();
      Notes note =
          context
              .getService()
              .run(
                  Select.from(NOTES)
                      .columns(n -> n.address_businessPartner(), n -> n.address_ID())
                      .matching(noteKeys))
              .single();
      CqnSelect addressOfNote =
          CQL.copy(
              context.getCqn(),
              new Modifier() {

                @Override
                public CqnStructuredTypeRef ref(CqnStructuredTypeRef ref) {
                  return CQL.entity(ADDRESSES)
                      .filter(
                          p ->
                              p.businessPartner()
                                  .eq(note.getAddressBusinessPartner())
                                  .and(p.ID().eq(note.getAddressId())))
                      .asRef();
                }
              });
      context.setResult(context.getService().run(addressOfNote));
      return;
    }

    // notes expanded?
    AtomicReference<CqnExpand> notesExpandHolder = new AtomicReference<>();
    CqnSelect noNotesExpand =
        CQL.copy(
            context.getCqn(),
            new Modifier() {

              public List<CqnSelectListItem> items(List<CqnSelectListItem> items) {
                notesExpandHolder.set(removeIfExpanded(items, Addresses.NOTES));
                return ensureSelected(items, Addresses.BUSINESS_PARTNER, Addresses.ID);
              }
            });

    // read addresses
    var addresses = CdsResult.of(bupa.run(noNotesExpand), Addresses.class);

    // add expanded notes?
    CqnExpand notesExpand = notesExpandHolder.get();
    if (notesExpand != null) {
      var notesSelect =
          Select.from(NOTES)
              .columns(
                  ensureSelected(
                      notesExpand.items(), Notes.ADDRESS_BUSINESS_PARTNER, Notes.ADDRESS_ID))
              .orderBy(notesExpand.orderBy())
              .where(
                  n ->
                      CQL.or(
                              addresses.stream()
                                  .map(
                                      address ->
                                          n.address_businessPartner()
                                              .eq(address.getBusinessPartner())
                                              .and(n.address_ID().eq(address.getId())))
                                  .collect(Collectors.toList()))
                          .and(predicate(notesExpand.ref().rootSegment())));

      var notes = context.getService().run(notesSelect);
      for (Addresses address : addresses) {
        address.setNotes(
            notes.stream()
                .filter(
                    n ->
                        n.getAddressBusinessPartner().equals(address.getBusinessPartner())
                            && n.getAddressId().equals(address.getId()))
                .collect(Collectors.toList()));
      }
    }

    context.setResult(addresses);
  }

  @On(entity = Notes_.CDS_NAME)
  void readNotes(CdsReadEventContext context) {
    List<? extends Segment> segments = context.getCqn().ref().segments();
    // via addresses
    if (segments.size() == 2 && segments.getFirst().id().equals(Addresses_.CDS_NAME)) {
      Map<String, Object> addressKeys = analyzer.analyze(context.getCqn()).rootKeys();
      CqnSelect notesOfAddress =
          CQL.copy(
              context.getCqn(),
              new Modifier() {

                @Override
                public CqnStructuredTypeRef ref(CqnStructuredTypeRef ref) {
                  return CQL.entity(NOTES).filter(predicate(segments.get(1))).asRef();
                }

                @Override
                public Predicate where(Predicate where) {
                  Predicate ofAddress =
                      CQL.get(Notes.ADDRESS_BUSINESS_PARTNER)
                          .eq(addressKeys.get(Addresses.BUSINESS_PARTNER))
                          .and(CQL.get(Notes.ADDRESS_ID).eq(addressKeys.get(Addresses.ID)));
                  if (where != null) {
                    ofAddress = ofAddress.and(where);
                  }
                  return ofAddress;
                }
              });
      context.setResult(context.getService().run(notesOfAddress));
      return;
    }

    // address expanded?
    AtomicReference<CqnExpand> addressExpandHolder = new AtomicReference<>();
    CqnSelect noAddressExpand =
        CQL.copy(
            context.getCqn(),
            new Modifier() {

              public List<CqnSelectListItem> items(List<CqnSelectListItem> items) {
                addressExpandHolder.set(removeIfExpanded(items, Notes.ADDRESS));
                return ensureSelected(items, Notes.ADDRESS_BUSINESS_PARTNER, Notes.ADDRESS_ID);
              }
            });

    CqnExpand addressExpand = addressExpandHolder.get();
    if (addressExpand != null) {
      // read notes and join with addresses
      var notes = CdsResult.of(context.getService().run(noAddressExpand), Notes.class);
      List<Notes> notesWithAddresses =
          notes.stream()
              .filter(n -> n.getAddressBusinessPartner() != null && n.getAddressId() != null)
              .collect(Collectors.toList());
      if (notesWithAddresses.size() > 0) {
        var addressSelect =
            Select.from(ADDRESSES)
                .columns(
                    ensureSelected(addressExpand.items(), Addresses.BUSINESS_PARTNER, Addresses.ID))
                .orderBy(addressExpand.orderBy())
                .where(
                    a ->
                        CQL.or(
                                notesWithAddresses.stream()
                                    .map(
                                        n ->
                                            a.businessPartner()
                                                .eq(n.getAddressBusinessPartner())
                                                .and(a.ID().eq(n.getAddressId())))
                                    .collect(Collectors.toList()))
                            .and(predicate(addressExpand.ref().rootSegment())));

        var addresses = context.getService().run(addressSelect);
        for (Notes note : notes) {
          note.setAddress(
              addresses.stream()
                  .filter(
                      a ->
                          a.getBusinessPartner().equals(note.getAddressBusinessPartner())
                              && a.getId().equals(note.getAddressId()))
                  .findFirst()
                  .orElse(null));
        }
      }
      context.setResult(notes);
      return;
    }
  }

  private CqnExpand removeIfExpanded(List<CqnSelectListItem> items, String association) {
    CqnExpand expanded =
        items.stream()
            .filter(i -> i.isExpand())
            .map(i -> i.asExpand())
            .filter(i -> i.ref().firstSegment().equals(association))
            .findFirst()
            .orElse(null);
    if (expanded != null) {
      items.remove(expanded);
    }
    return expanded;
  }

  private List<CqnSelectListItem> ensureSelected(
      List<CqnSelectListItem> items, String... elements) {
    if (items.stream().anyMatch(i -> i.isStar())) {
      return items;
    }
    Set<String> newElements = new HashSet<>();
    for (String element : elements) {
      if (!items.stream().anyMatch(i -> i.isValue() && i.asValue().displayName().equals(element))) {
        newElements.add(element);
      }
    }
    List<CqnSelectListItem> newItems = new ArrayList<>(items);
    newElements.forEach(element -> newItems.add(CQL.get(element)));
    return newItems;
  }

  private CqnPredicate predicate(Segment segment) {
    return segment.filter().orElse(CQL.constant(true).eq(true));
  }
}
