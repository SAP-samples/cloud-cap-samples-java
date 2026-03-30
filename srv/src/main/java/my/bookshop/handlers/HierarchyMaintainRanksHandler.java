package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.GENRE_HIERARCHY;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy;
import cds.gen.adminservice.GenreHierarchy_;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Maintains contiguous 1-based {@code siblingRank} values when a node is reparented (UPDATE with
 * changed {@code parent_ID}) or deleted.
 *
 * <p>On reparent:
 *
 * <ul>
 *   <li>Old parent's children: close the gap left by the moved node.
 *   <li>New parent's children: shift all ranks up by 1, assign rank 1 to the moved node.
 * </ul>
 *
 * <p>On delete: close the gap left by the removed node among its siblings.
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
public class HierarchyMaintainRanksHandler implements EventHandler {

  private final PersistenceService db;

  HierarchyMaintainRanksHandler(PersistenceService db) {
    this.db = db;
  }

  /** Before UPDATE: detect parent_ID change and adjust siblingRanks accordingly. */
  @Before(event = CqnService.EVENT_UPDATE, entity = GenreHierarchy_.CDS_NAME)
  void onReparent(GenreHierarchy data, GenreHierarchy_ ref) {
    // Resolve new parent ID from either flat foreign key or nested association
    String newParentId;
    if (data.containsKey(GenreHierarchy.PARENT_ID)) {
      newParentId = data.getParentId();
    } else if (data.containsKey(GenreHierarchy.PARENT) && data.getParent() != null) {
      newParentId = data.getParent().getId();
    } else {
      return;
    }

    // Read the current state of the node
    GenreHierarchy current =
        db.run(Select.from(ref).columns(c -> c.ID(), c -> c.siblingRank(), c -> c.parent_ID()))
            .single();

    String oldParentId = current.getParentId();

    if (Objects.equals(oldParentId, newParentId)) {
      return;
    }

    int oldRank = current.getSiblingRank();

    // Close the gap in the old parent's children
    closeGap(oldParentId, oldRank);

    // Shift all children of the new parent up by 1 to make room at rank 1
    db.run(
        Update.entity(GENRE_HIERARCHY)
            .where(c -> c.parent_ID().eq(newParentId))
            .set(b -> b.siblingRank(), s -> s.plus(1)));

    // Assign rank 1 to the moved node
    data.setSiblingRank(1);
  }

  /** Before DELETE: read the node's parent and rank so we can close the gap after deletion. */
  @Before(event = CqnService.EVENT_DELETE, entity = GenreHierarchy_.CDS_NAME)
  void beforeDelete(GenreHierarchy_ ref) {
    GenreHierarchy current =
        db.run(Select.from(ref).columns(c -> c.parent_ID(), c -> c.siblingRank())).single();
    // Store in thread-local for the @After handler
    deletedParentId.set(current.getParentId());
    deletedRank.set(current.getSiblingRank());
  }

  /** After DELETE: close the gap left by the deleted node. */
  @After(event = CqnService.EVENT_DELETE, entity = GenreHierarchy_.CDS_NAME)
  void afterDelete() {
    String parentId = deletedParentId.get();
    Integer rank = deletedRank.get();
    deletedParentId.remove();
    deletedRank.remove();

    if (parentId != null && rank != null) {
      closeGap(parentId, rank);
    }
  }

  /**
   * Decrements the siblingRank of all siblings with a rank greater than {@code removedRank} under
   * the given parent, closing the gap left by a removed node.
   */
  private void closeGap(String parentId, int removedRank) {
    db.run(
        Update.entity(GENRE_HIERARCHY)
            .where(c -> c.parent_ID().eq(parentId).and(c.siblingRank().gt(removedRank)))
            .set(b -> b.siblingRank(), s -> s.minus(1)));
  }

  private static final ThreadLocal<String> deletedParentId = new ThreadLocal<>();
  private static final ThreadLocal<Integer> deletedRank = new ThreadLocal<>();
}
