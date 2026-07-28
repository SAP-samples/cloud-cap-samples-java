package my.bookshop.handlers;

import com.sap.cds.CdsData;
import com.sap.cds.CdsDataProcessor;
import com.sap.cds.services.draft.DraftCreateEventContext;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.UserInfo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ServiceName(value = "*", type = DraftService.class)
public class UserDescriptionHandler implements EventHandler {

  @Autowired private UserInfo userInfo;

  @Before
  protected void addDraftFields(DraftCreateEventContext context, List<CdsData> entries) {
    CdsDataProcessor.Filter filter =
        (path, element, type) -> "InProcessByUserDescription".equals(element.getName());
    CdsDataProcessor.Generator generator = (path, element, isNull) -> description();
    CdsDataProcessor.create()
        .addGenerator(filter, generator)
        .process(entries, context.getTarget());
  }

  private String description() {
    return userInfo.getAdditionalAttribute("given_name")
        + " "
        + userInfo.getAdditionalAttribute("family_name");
  }
}
