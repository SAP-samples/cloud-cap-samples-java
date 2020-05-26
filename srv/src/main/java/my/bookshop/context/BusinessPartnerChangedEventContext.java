package my.bookshop.context;

import java.util.List;

import com.sap.cds.CdsData;
import com.sap.cds.ql.CdsName;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.EventName;

/**
 * In the future CAP will generate these interfaces based on the events defined in the CDS model
 */
@EventName("BO.BusinessPartner.Changed")
public interface BusinessPartnerChangedEventContext extends EventContext {

	BoBusinessPartnerChanged getData();

	void setData(BoBusinessPartnerChanged data);

	@CdsName("API_BUSINESS_PARTNER.BO.BusinessPartner.Changed")
	public interface BoBusinessPartnerChanged extends CdsData {

		@CdsName("KEY")
		List<BusinessPartnerKey> getKeys();

		@CdsName("KEY")
		void setKeys(List<BusinessPartnerKey> keys);

		public interface BusinessPartnerKey extends CdsData {

			@CdsName("BUSINESSPARTNER")
			String getBusinessPartner();

			@CdsName("BUSINESSPARTNER")
			void setBusinessPartner(String businessPartner);

		}

	}

}
