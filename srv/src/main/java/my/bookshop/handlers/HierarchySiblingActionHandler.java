package my.bookshop.handlers;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnSelect;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy;
import cds.gen.adminservice.GenreHierarchyMoveSiblingContext;
import cds.gen.adminservice.GenreHierarchy_;

@Component
@ServiceName(AdminService_.CDS_NAME)
/**
 * Example of a custom handler for nextSiblingAction
 */
@Profile("cloud")
public class HierarchySiblingActionHandler implements EventHandler {

    private final PersistenceService db;

    HierarchySiblingActionHandler(PersistenceService db) {
        this.db = db;
    }

    @On(entity = GenreHierarchy_.CDS_NAME)
    void onMoveSiblingAction(GenreHierarchyMoveSiblingContext event) {
        CqnSelect select = event.getCqn();
        // Get ID of the entry that is being moved
        String idToMove = (String) CqnAnalyzer.create(event.getModel()).analyze(select).targetKeys()
                .get(GenreHierarchy_.ID);
        // Find its' parent
        String parentId = db.run(Select.from(GenreHierarchy_.class)
                .columns(c -> c.parent_ID()).where(c -> c.ID().eq(idToMove)))
                .single(GenreHierarchy.class).getParentId();
        // Find all children of the parent, which are siblings of the entry being moved
        List<GenreHierarchy> siblingNodes = db.run(Select.from(GenreHierarchy_.class)
                .columns(c -> c.ID(), c -> c.siblingRank())
                .where(c -> c.parent_ID().eq(parentId)))
                .listOf(GenreHierarchy.class);

        String nextSiblingId = event.getNextSibling() == null ? null : event.getNextSibling().getId();
        Optional<GenreHierarchy> nextSibling = siblingNodes.stream().filter(el -> el.getId().equals(nextSiblingId))
                .findFirst();

        GenreHierarchy nodeToMove = siblingNodes.stream().filter(el -> idToMove.equals(el.getId())).findFirst().get();
        GenreHierarchy moved = siblingNodes.remove(siblingNodes.indexOf(nodeToMove));
        // Exchange siblings
        nextSibling.ifPresentOrElse(n -> siblingNodes.add(siblingNodes.indexOf(n), moved),
                () -> siblingNodes.addLast(moved));
        
        // Apply ranks
        int i = 0;
        for(GenreHierarchy sibling : siblingNodes) {
            sibling.setSiblingRank(i++);
        }

        // Update DB
        db.run(Update.entity(GenreHierarchy_.class).entries(siblingNodes));
        event.setCompleted();
    }
}
