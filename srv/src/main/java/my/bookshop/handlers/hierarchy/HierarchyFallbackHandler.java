package my.bookshop.handlers.hierarchy;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy_;

@Component
@Profile("default") // non-HANA
@ServiceName(AdminService_.CDS_NAME)
/**
 * Fallback to plain representation
 */
public class HierarchyFallbackHandler implements EventHandler {

    private final PersistenceService db;

    HierarchyFallbackHandler(PersistenceService db) {
        this.db = db;
    }

    @Before(event = CqnService.EVENT_READ, entity = GenreHierarchy_.CDS_NAME)
    public void readGenreHierarchy(CdsReadEventContext event) {
        if (event.getCqn().transformations().size() < 1) {
            return;
        }
        event.setResult(db.run(Select.from(GenreHierarchy_.class)));
    }

}
