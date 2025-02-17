package my.bookshop.handlers;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy;
import cds.gen.adminservice.GenreHierarchyMoveSiblingActionContext;
import cds.gen.adminservice.GenreHierarchy_;


@Component
@ServiceName(AdminService_.CDS_NAME)
/**
 *  Example of a custom handler for nextSiblingAction
 */
@Profile("hybrid")
public class HierarchySiblingActionHandler implements EventHandler {

    private final PersistenceService db;

    HierarchySiblingActionHandler(PersistenceService db) {
        this.db = db;
    }

    @On(entity = GenreHierarchy_.CDS_NAME)
    void onMoveSiblingAction(GenreHierarchyMoveSiblingActionContext event) {
        CqnSelect select = event.getCqn();
        String toMoveId = (String) CqnAnalyzer.create(event.getModel()).analyze(select).targetKeys().get(GenreHierarchy_.ID);
        CqnSelect parentCQN = Select.from(GenreHierarchy_.class).columns(c -> c.parent_ID()).where(c -> c.ID().eq(toMoveId));
        GenreHierarchy parentNode = db.run(parentCQN).single(GenreHierarchy.class);
        CqnSelect childrenCQN = Select.from(GenreHierarchy_.class).columns(c -> c.ID(), c -> c.siblingRank()).where(c -> c.parent_ID().eq(parentNode.getParentId()));
        List<GenreHierarchy> siblingNodes = db.run(childrenCQN).listOf(GenreHierarchy.class);
        
        List<Integer> siblingRanks = siblingNodes.stream().map(ch -> ch.getSiblingRank()).toList();
        
        GenreHierarchy nodeToMove = siblingNodes.stream().filter(el -> toMoveId.equals(el.getId())).findFirst().get();
        String nextSiblingId = event.getNextSibling() == null ? null : event.getNextSibling().getId();
        Optional <GenreHierarchy> nextSibling = siblingNodes.stream().filter(el -> el.getId().equals(nextSiblingId)).findFirst();

        GenreHierarchy moved = siblingNodes.remove(siblingNodes.indexOf(nodeToMove));
        nextSibling.ifPresentOrElse(n -> siblingNodes.add(siblingNodes.indexOf(n), moved), () -> siblingNodes.addLast(moved));
        
        for (int i=0; i < siblingRanks.size(); i++) {
            siblingNodes.get(i).setSiblingRank(siblingRanks.get(i));
        }

        CqnUpdate updateCQN = Update.entity(GenreHierarchy_.class).entries(siblingNodes);
        db.run(updateCQN);
        event.setCompleted();
    }
}
