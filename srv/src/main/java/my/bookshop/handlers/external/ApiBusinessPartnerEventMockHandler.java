package my.bookshop.handlers.external;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.services.cds.ApplicationService;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messaging.MessagingService;

import cds.gen.api_business_partner.ABusinessPartnerAddress;
import cds.gen.api_business_partner.ABusinessPartnerAddress_;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.api_business_partner.BOBusinessPartnerChanged;

/**
 * This class mocks the event emitting of the S/4 API
 */
@Component
@Profile("!cloud")
@ServiceName(value = { ApiBusinessPartner_.CDS_NAME, ApiBusinessPartner_.CDS_NAME + "_MOCKED"}, type = ApplicationService.class)
public class ApiBusinessPartnerEventMockHandler implements EventHandler {

	private final static Logger logger = LoggerFactory.getLogger(ApiBusinessPartnerEventMockHandler.class);

	@Autowired
	private MessagingService bupaMessaging;

	@After(event = CqnService.EVENT_UPDATE, entity = ABusinessPartnerAddress_.CDS_NAME)
	public void businessPartnerChanged(CdsUpdateEventContext context) {
		// Get BusinessPartner ID
		CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
		String businessPartner = (String) analyzer.analyze(context.getCqn().ref()).targetKeys().get(ABusinessPartnerAddress.BUSINESS_PARTNER);

		// Construct S/4 HANA Payload
		BOBusinessPartnerChanged eventData = BOBusinessPartnerChanged.create();
		BOBusinessPartnerChanged.Key eventBupaKey = BOBusinessPartnerChanged.Key.create();
		eventBupaKey.setBusinesspartner(businessPartner);
		eventData.setKey(Arrays.asList(eventBupaKey));

		// Emit Changed Event
		String payload = "{\"data\": " + eventData.toJson() + "}"; // cloud events structure
		logger.info("<< emitting: " + payload);
		bupaMessaging.emit("BO/BusinessPartner/Changed", payload);
	}


}
