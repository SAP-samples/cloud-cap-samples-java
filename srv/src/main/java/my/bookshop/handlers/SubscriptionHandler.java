package my.bookshop.handlers;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.mt.MtSubscriptionService;
import com.sap.cds.services.mt.MtUnsubscribeEventContext;

/**
 * Handler that implements subscription logic
 */
@Component
@Profile("cloud")
@ServiceName(MtSubscriptionService.DEFAULT_NAME)
public class SubscriptionHandler implements EventHandler {

	@Before(event = MtSubscriptionService.EVENT_UNSUBSCRIBE)
	public void beforeUnsubscribe(MtUnsubscribeEventContext context) {
		// always delete the subscription
		context.setDelete(true);
	}

}
