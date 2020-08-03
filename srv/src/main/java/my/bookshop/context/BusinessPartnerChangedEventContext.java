package my.bookshop.context;

import com.sap.cds.services.EventContext;
import com.sap.cds.services.EventName;

import cds.gen.api_business_partner.bo.businesspartner.Changed;

/**
 * In the future CAP will generate these interfaces based on the events defined in the CDS model
 */
@EventName("BO.BusinessPartner.Changed")
public interface BusinessPartnerChangedEventContext extends EventContext {

	Changed getData();

	void setData(Changed data);

}
