package my.bookshop.handlers;

import com.sap.cds.feature.ucl.services.AssignEventContext;
import com.sap.cds.feature.ucl.services.SpiiResult;
import com.sap.cds.feature.ucl.services.UclService;
import com.sap.cds.services.CoreFactory;
import com.sap.cds.services.ServiceExceptionUtils;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.utils.CdsErrorStatuses;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
@ServiceName(UclService.DEFAULT_NAME)
public class CustomUclHandler implements EventHandler {

  @On
  public void assign(AssignEventContext ctx) {

    ServiceExceptionUtils utils = CoreFactory.INSTANCE.createServiceExceptionUtils();

    String msg =
        utils.getLocalizedMessage(
            CdsErrorStatuses.AUDITLOG_NOT_READABLE.getCodeString(),
            new Object[] {"value"},
            Locale.getDefault());

    SpiiResult result = SpiiResult.create();
    result.put("message", msg); // no real benefit, but just to have another line of code
    ctx.setUclResult(result);
    ctx.setCompleted();
  }
}
