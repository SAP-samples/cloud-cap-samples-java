package my.bookshop.handlers;


import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.services.EventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Books_;

@Component
@ServiceName(AdminService_.CDS_NAME)
/**
 * Example of a custom handler for nextSiblingAction
 */
@Profile("hybrid")
public class SetHiddenHandler implements EventHandler {

    @After(entity = Books_.CDS_NAME)   
    void removeUIHidden(EventContext context){
        Object result = context.get("result");
        if (result instanceof Map row) {
            row.put("contentsHidden", true);
        } else if (result instanceof List rows && rows.size() == 1) {
            ((Map)rows.get(0)).put("contentsHidden", true);
        }
    }
}

  