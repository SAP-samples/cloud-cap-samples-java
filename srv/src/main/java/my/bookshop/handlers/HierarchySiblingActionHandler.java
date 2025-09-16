package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.GENRE_HIERARCHY;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy;
import cds.gen.adminservice.GenreHierarchyMoveSiblingContext;
import cds.gen.adminservice.GenreHierarchy_;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@ServiceName(AdminService_.CDS_NAME)
/**
 * Example of a custom handler for nextSiblingAction
 */
public class HierarchySiblingActionHandler implements EventHandler {

    private final PersistenceService db;

    HierarchySiblingActionHandler(PersistenceService db) {
        this.db = db;
    }

    @On
    void onMoveSiblingAction(GenreHierarchy_ ref, GenreHierarchyMoveSiblingContext context) {
        // Find current node and its parent
        GenreHierarchy toMove = db.run(Select.from(ref)
                .columns(c -> c.ID(), c -> c.parent_ID()))
                .single();

        // Find all children of the parent, which are siblings of the entry being moved
        List<GenreHierarchy> siblingNodes = db.run(Select.from(GENRE_HIERARCHY)
                .columns(c -> c.ID(), c -> c.siblingRank())
                .where(c -> c.parent_ID().eq(toMove.getParentId())))
                .list();

        int oldPosition = 0;
        int newPosition = siblingNodes.size();
        for (int i = 0; i < siblingNodes.size(); ++i) {
            GenreHierarchy sibling = siblingNodes.get(i);
            if (sibling.getId().equals(toMove.getId())) {
                oldPosition = i;
            }
            if (context.getNextSibling() != null && sibling.getId().equals(context.getNextSibling().getId())) {
                newPosition = i;
            }
        }

        // Move siblings
        siblingNodes.add(oldPosition < newPosition ? newPosition - 1 : newPosition, siblingNodes.remove(oldPosition));

        // Recalculate ranks
        for (int i = 0; i < siblingNodes.size(); ++i) {
            siblingNodes.get(i).setSiblingRank(i);
        }

        // Update DB
        db.run(Update.entity(GENRE_HIERARCHY).entries(siblingNodes));
        context.setCompleted();
    }
}
