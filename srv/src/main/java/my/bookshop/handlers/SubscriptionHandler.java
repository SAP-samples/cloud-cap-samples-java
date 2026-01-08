package my.bookshop.handlers;

import com.sap.cds.services.auditlog.AuditLogService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.mt.DeploymentService;
import com.sap.cds.services.mt.SubscribeEventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Handler that implements subscription logic */
@Component
@Profile("cloud")
@ServiceName(DeploymentService.DEFAULT_NAME)
class SubscriptionHandler implements EventHandler {

  @Autowired private AuditLogService auditLog;

  @After
  public void afterSubscribe(SubscribeEventContext context) {
    String msg = "New tenant '%s' subscribed.".formatted(context.getTenant());

    // send audit log security message to provider tenant as user's tenant is null
    auditLog.logSecurityEvent("tenant subscribed", msg);
  }
}
