package my.bookshop.handlers;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.ql.cqn.transformation.CqnAncestorsTransformation;
import com.sap.cds.ql.cqn.transformation.CqnTransformation;
import com.sap.cds.services.cds.CdsReadEventContext;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy_;



@Component
@ServiceName(AdminService_.CDS_NAME)
/**
 *  For testing purposes of modifying requests for hierarchies
 *  Remove when generic solution for $apply and $expand is available
 */
@Profile("hybrid")
public class HierarchyExpandHandler implements EventHandler {

//    @On(entity = GenreHierarchy_.CDS_NAME)
    void removeExpand(CdsReadEventContext event) {
        List<CqnTransformation> trafos = event.getCqn().transformations();
        if (trafos.size() < 1) {
            return;
        }
        if (trafos.get(0) instanceof CqnAncestorsTransformation) {
            CqnSelect original = event.getCqn();
            Boolean isExpand = original.items().stream().filter(CqnSelectListItem::isExpand).findAny().isPresent();
            if (isExpand) {   
                CqnSelect copy = CQL.copy(original, new Modifier() {
                    public List<CqnSelectListItem> items(List<CqnSelectListItem> items) {
                        return items.stream().filter(i -> !i.isExpand()).toList();
                    }
                });
            event.setCqn(copy);
            }
        }
        event.proceed(); 
    }
}
