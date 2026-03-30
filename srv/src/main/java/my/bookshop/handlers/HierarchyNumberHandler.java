package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.GENRE_HIERARCHY;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy;
import cds.gen.adminservice.GenreHierarchy_;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Computes the virtual {@code number} element of {@link GenreHierarchy} on read.
 *
 * <p>The number is the dot-separated path of {@code siblingRank} values from the root to the node,
 * e.g. {@code "2.20.1"} for a node at depth 3.
 *
 * <p>Only the ancestors that are actually needed are loaded from the database, one query per tree
 * level. This scales well for large data sets with bounded tree depth.
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
public class HierarchyNumberHandler implements EventHandler {

  private final PersistenceService db;

  HierarchyNumberHandler(PersistenceService db) {
    this.db = db;
  }

  /**
   * Ensures that {@code siblingRank} is always included in the SELECT columns. This is needed as a
   * workaround for a cds4j bug that renders wrong SQL when {@code siblingRank} is not selected.
   */
  @Before(event = CqnService.EVENT_READ, entity = GenreHierarchy_.CDS_NAME)
  void ensureSiblingRankSelected(CdsReadEventContext context) {
    CqnSelect query = context.getCqn();
    if (CqnAnalyzer.isCountQuery(query)) {
      return;
    }
    CqnSelect copy =
        CQL.copy(
            query,
            new Modifier() {
              @Override
              public List<CqnSelectListItem> items(List<CqnSelectListItem> items) {
                if (items.isEmpty()) {
                  return items;
                }
                boolean selectsSiblingRank =
                    items.stream()
                        .anyMatch(
                            sli ->
                                sli instanceof CqnElementRef ref
                                    && GenreHierarchy.SIBLING_RANK.equals(ref.path()));
                if (!selectsSiblingRank) {
                  List<CqnSelectListItem> newItems = new ArrayList<>(items);
                  newItems.add(CQL.get(GenreHierarchy.SIBLING_RANK));
                  return newItems;
                }
                return items;
              }
            });
    context.setCqn(copy);
  }

  @After(event = CqnService.EVENT_READ, entity = GenreHierarchy_.CDS_NAME)
  void computeNumber(List<GenreHierarchy> genres) {
    if (genres.isEmpty()) {
      return;
    }

    // Seed with IDs of result nodes
    Set<String> needed =
        genres.stream()
            .map(GenreHierarchy::getId)
            .filter(id -> id != null)
            .collect(Collectors.toCollection(HashSet::new));

    if (needed.isEmpty()) {
      return;
    }

    // Load result nodes and iteratively their ancestors, one round per tree level
    Map<String, GenreHierarchy> known = new HashMap<>();
    while (!needed.isEmpty()) {
      List<GenreHierarchy> loaded =
          db.run(
                  Select.from(GENRE_HIERARCHY)
                      .columns(c -> c.ID(), c -> c.siblingRank(), c -> c.parent_ID())
                      .where(c -> c.ID().in(needed)))
              .list();
      needed.clear();
      for (GenreHierarchy g : loaded) {
        known.put(g.getId(), g);
        if (g.getParentId() != null && !known.containsKey(g.getParentId())) {
          needed.add(g.getParentId());
        }
      }
    }

    // Compute the number for each result node
    for (GenreHierarchy g : genres) {
      if (g.getId() == null) {
        continue;
      }
      GenreHierarchy current = known.get(g.getId());
      if (current == null || current.getSiblingRank() == null) {
        continue;
      }
      String number = String.valueOf(current.getSiblingRank());
      current = current.getParentId() != null ? known.get(current.getParentId()) : null;
      while (current != null && current.getSiblingRank() != null) {
        number = current.getSiblingRank() + "." + number;
        current = current.getParentId() != null ? known.get(current.getParentId()) : null;
      }
      g.setNumber(number);
    }
  }
}
