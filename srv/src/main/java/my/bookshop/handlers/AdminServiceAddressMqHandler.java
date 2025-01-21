package my.bookshop.handlers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.HandlerOrder;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.adminservice.AdminService_;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.api_business_partner.BusinessPartnerChangedContext;

/**
 * Message Queuing feature does not provide Multi Tenancy support. To
 * demonstrate the feature though, this handler takes precedence over
 * {@link AdminServiceAddressHandler#updateBusinessPartnerAddresses(BusinessPartnerChangedContext)}
 * and wraps the event processing with a provider tenant level request context
 * so that the message will be consumed by the provider tenant.
 */
@Profile("mq-messaging-cloud")
@Component
@ServiceName(AdminService_.CDS_NAME)
public class AdminServiceAddressMqHandler implements EventHandler {

    @On(service = ApiBusinessPartner_.CDS_NAME)
    @HandlerOrder(HandlerOrder.EARLY)
    public void updateBusinessPartnerAddresses(BusinessPartnerChangedContext context) {
        context.getCdsRuntime().requestContext().systemUserProvider().run(req -> {
            context.proceed();
        });
    }
}
