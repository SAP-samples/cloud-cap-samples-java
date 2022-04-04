package my.bookshop.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.services.auditlog.AuditLogService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.mt.MtSubscribeEventContext;
import com.sap.cds.services.mt.MtSubscriptionService;
import com.sap.cds.services.mt.MtUnsubscribeEventContext;

/**
 * Handler that implements subscription logic
 */
@Component
@Profile("cloud")
@ServiceName(MtSubscriptionService.DEFAULT_NAME)
class SubscriptionHandler implements EventHandler {

	@Autowired
	private AuditLogService auditLog;

	@Before(event = MtSubscriptionService.EVENT_UNSUBSCRIBE)
	public void beforeUnsubscribe(MtUnsubscribeEventContext context) {
		// always delete the subscription
		context.setDelete(true);
	}

	@After(event = MtSubscriptionService.EVENT_SUBSCRIBE)
	public void afterSubscribe(MtSubscribeEventContext context) {
		String msg = String.format("New tenant '%s' subscribed.", context.getTenantId());

		// send audit log security message to provider tenant as user's tenant is null
		auditLog.logSecurityEvent("tenant subscribed", msg);
	}

}
