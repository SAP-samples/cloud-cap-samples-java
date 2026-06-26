package my.bookshop.handlers;

import com.sap.cds.CdsData;
import com.sap.cds.CdsDataProcessor;
import com.sap.cds.services.draft.DraftCreateEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.request.UserInfo;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserDescriptionHandler implements EventHandler {

  @On(service = "*")
  protected void addDraftFields(DraftCreateEventContext context, List<CdsData> entries) {
    CdsDataProcessor.Filter filter =
        (path, element, type) ->
            switch (element.getName()) {
              case "InProcessByUserDescription" -> true;
              default -> false;
            };

    UserInfo userInfo = context.getUserInfo();
    CdsDataProcessor.Generator generator =
        (path, element, isNull) -> userInfo.getAdditionalAttribute("nickname");
    CdsDataProcessor dataProcessor = CdsDataProcessor.create().addGenerator(filter, generator);

    dataProcessor.process(entries, context.getTarget());
  }
}
