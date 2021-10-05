package my.bookshop;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.changeset.ChangeSetListener;

import cds.gen.adminservice.Orders;
import cds.gen.api_business_partner.ABusinessPartnerAddress;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.api_business_partner.BusinessPartnerChangedContext;

public class AdminServiceAddressITestBase {

	private static final String ordersURI = "/api/admin/Orders";
	private static final String orderURI = ordersURI + "(IsActiveEntity=true,ID=%s)";
	private static final String addressesURI = "/api/admin/Addresses";
	private static final String remoteAddressURI = "/api/API_BUSINESS_PARTNER/A_BusinessPartnerAddress(BusinessPartner='%s',AddressID='%s')";

	@Autowired
	private WebTestClient client;

	@Autowired
	@Qualifier(ApiBusinessPartner_.CDS_NAME)
	private CqnService bupa;

	public void testAddressesValueHelp() {
		client.get().uri(addressesURI).headers(this::adminCredentials).exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.['@context']").isEqualTo("$metadata#Addresses")
				.jsonPath("$.value[0].ID").isEqualTo("100")
				.jsonPath("$.value[0].businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[1].ID").isEqualTo("200")
				.jsonPath("$.value[1].businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[2].ID").isEqualTo("300")
				.jsonPath("$.value[2].businessPartner").isEqualTo("10401010");
	}

	public void testOrderWithAddress() throws InterruptedException {
		Orders order = Orders.create();
		order.setOrderNo("1337");
		order.setShippingAddressId("100");

		String id = UUID.randomUUID().toString();
		client.put().uri(String.format(orderURI, id))
				.headers(this::adminCredentials)
				.header("Content-Type", "application/json")
				.bodyValue(order.toJson())
				.exchange()
				.expectStatus().isCreated();

		client.get().uri(String.format(orderURI, id) + "?$expand=shippingAddress").headers(this::adminCredentials).exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.ID").isEqualTo(id)
				.jsonPath("$.OrderNo").isEqualTo(order.getOrderNo())
				.jsonPath("$.shippingAddress.ID").isEqualTo("100")
				.jsonPath("$.shippingAddress.businessPartner").isEqualTo("10401010")
				.jsonPath("$.shippingAddress.houseNumber").isEqualTo("16");

		client.get().uri(String.format(orderURI, id) + "/shippingAddress").headers(this::adminCredentials).exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.ID").isEqualTo("100")
				.jsonPath("$.businessPartner").isEqualTo("10401010")
				.jsonPath("$.houseNumber").isEqualTo("16");

		// react on remote address update
		CountDownLatch latch = new CountDownLatch(1);
		bupa.on(BusinessPartnerChangedContext.CDS_NAME, null, (context) -> context.getChangeSetContext().register(new ChangeSetListener(){

			@Override
			public void afterClose(boolean completed) {
				latch.countDown();
			}

		}));

		// update remote address
		ABusinessPartnerAddress address = ABusinessPartnerAddress.create();
		address.setHouseNumber("17");

		client.patch().uri(String.format(remoteAddressURI, "10401010", "100"))
				.header("Content-Type", "application/json")
				.bodyValue(address.toJson())
				.exchange()
				.expectStatus().isOk();

		// wait for remote address update
		latch.await(30, TimeUnit.SECONDS);
		client.get().uri(String.format(orderURI, id) + "/shippingAddress").headers(this::adminCredentials).exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.ID").isEqualTo("100")
				.jsonPath("$.businessPartner").isEqualTo("10401010")
				.jsonPath("$.houseNumber").isEqualTo("17");
	}

	private void adminCredentials(HttpHeaders headers) {
		headers.setBasicAuth("admin", "admin");
	}

}
