package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.GENRE_HIERARCHY;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy;
import cds.gen.adminservice.GenreHierarchyMoveSiblingContext;
import cds.gen.adminservice.GenreHierarchy_;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.stereotype.Component;

@Component
@ServiceName(AdminService_.CDS_NAME)
/**
 * Handles the moveSibling action for GenreHierarchy.
 *
 * <p>Assumes contiguous 1-based siblingRank values among siblings. Instead of loading all siblings
 * and rewriting them, this implementation uses expression-based updates to shift only the affected
 * ranks.
 */
public class HierarchySiblingActionHandler implements EventHandler {

  private final PersistenceService db;

  HierarchySiblingActionHandler(PersistenceService db) {
    this.db = db;
  }

  @On
  void onMoveSiblingAction(GenreHierarchy_ ref, GenreHierarchyMoveSiblingContext context) {
    // Get current node's rank and parent
    GenreHierarchy toMove =
        db.run(Select.from(ref).columns(c -> c.ID(), c -> c.siblingRank(), c -> c.parent_ID()))
            .single();

    String parentId = toMove.getParentId();
    int oldRank = toMove.getSiblingRank();

    // Determine target rank
    int newRank;
    if (context.getNextSibling() != null) {
      // Move before the specified next sibling
      GenreHierarchy nextSibling =
          db.run(
                  Select.from(GENRE_HIERARCHY)
                      .columns(c -> c.siblingRank())
                      .where(c -> c.ID().eq(context.getNextSibling().getId())))
              .single();
      newRank = nextSibling.getSiblingRank();
    } else {
      // Move to end: target rank is one past the current maximum
      Number siblingCount =
          db.run(
                  Select.from(GENRE_HIERARCHY)
                      .columns(b -> CQL.count().as("cnt"))
                      .where(c -> c.parent_ID().eq(parentId)))
              .single()
              .getPath("cnt");
      newRank = siblingCount.intValue() + 1;
    }

    if (oldRank == newRank || oldRank + 1 == newRank) {
      // Node is already at the target position
      context.setCompleted();
      return;
    }

    int targetRank;
    if (oldRank < newRank) {
      // Moving forward: shift siblings in (oldRank, newRank) down by 1
      db.run(
          Update.entity(GENRE_HIERARCHY)
              .where(
                  c ->
                      c.parent_ID()
                          .eq(parentId)
                          .and(c.siblingRank().gt(oldRank))
                          .and(c.siblingRank().lt(newRank)))
              .set(b -> b.siblingRank(), s -> s.minus(1)));
      targetRank = newRank - 1;
    } else {
      // Moving backward: shift siblings in [newRank, oldRank) up by 1
      db.run(
          Update.entity(GENRE_HIERARCHY)
              .where(
                  c ->
                      c.parent_ID()
                          .eq(parentId)
                          .and(c.siblingRank().ge(newRank))
                          .and(c.siblingRank().lt(oldRank)))
              .set(b -> b.siblingRank(), s -> s.plus(1)));
      targetRank = newRank;
    }

    // Set the moved node's new rank
    db.run(
        Update.entity(GENRE_HIERARCHY)
            .where(c -> c.ID().eq(toMove.getId()))
            .data("siblingRank", targetRank));

    context.setCompleted();
  }
}
