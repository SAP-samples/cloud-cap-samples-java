package my.bookshop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"default", "mocked"})
public class NotesServiceITest {

	private static final String notesURI = "/api/notes/Notes";
	private static final String addressesURI = "/api/notes/Addresses";

	@Autowired
	private WebTestClient client;

	@Test
	public void testGetNotes() throws Exception {
		client.get().uri(notesURI).exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.['@context']").isEqualTo("$metadata#Notes")
				.jsonPath("$.value[0].ID").isEqualTo("5efc842c-c70d-4ee2-af1d-81c7d257aff7")
				.jsonPath("$.value[0].note").isEqualTo("Ring at building 8")
				.jsonPath("$.value[0].address_businessPartner").isEqualTo("1000020")
				.jsonPath("$.value[0].address_ID").isEqualTo("500")
				.jsonPath("$.value[1].ID").isEqualTo("83e2643b-aecc-47d3-9f85-a8ba14eff07d")
				.jsonPath("$.value[1].note").isEqualTo("Packages can be dropped off at the reception")
				.jsonPath("$.value[1].address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[1].address_ID").isEqualTo("100")
				.jsonPath("$.value[2].ID").isEqualTo("880147b0-8d2d-4ef8-bb52-ae5ae6002fc5")
				.jsonPath("$.value[2].note").isEqualTo("Don't deliver packages after 5pm")
				.jsonPath("$.value[2].address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[2].address_ID").isEqualTo("100");
	}

	@Test
	public void testGetAddresses() throws Exception {
		client.get().uri(addressesURI + "?$filter=businessPartner eq '10401010'").exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.['@context']").isEqualTo("$metadata#Addresses")
				.jsonPath("$.value[0].ID").isEqualTo("100")
				.jsonPath("$.value[0].postalCode").isEqualTo("68199")
				.jsonPath("$.value[1].ID").isEqualTo("200")
				.jsonPath("$.value[1].postalCode").isEqualTo("68789")
				.jsonPath("$.value[2].ID").isEqualTo("300")
				.jsonPath("$.value[2].postalCode").isEqualTo("14469");
	}

	@Test
	public void testGetNoteWithAddress() throws Exception {
		client.get().uri(notesURI + "?$expand=address").exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.['@context']").isEqualTo("$metadata#Notes(address())")
				.jsonPath("$.value[0].ID").isEqualTo("5efc842c-c70d-4ee2-af1d-81c7d257aff7")
				.jsonPath("$.value[0].note").isEqualTo("Ring at building 8")
				.jsonPath("$.value[0].address_businessPartner").isEqualTo("1000020")
				.jsonPath("$.value[0].address_ID").isEqualTo("500")
				.jsonPath("$.value[0].address.businessPartner").isEqualTo("1000020")
				.jsonPath("$.value[0].address.ID").isEqualTo("500")
				.jsonPath("$.value[0].address.postalCode").isEqualTo("94304")
				.jsonPath("$.value[1].ID").isEqualTo("83e2643b-aecc-47d3-9f85-a8ba14eff07d")
				.jsonPath("$.value[1].note").isEqualTo("Packages can be dropped off at the reception")
				.jsonPath("$.value[1].address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[1].address_ID").isEqualTo("100")
				.jsonPath("$.value[1].address.businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[1].address.ID").isEqualTo("100")
				.jsonPath("$.value[1].address.postalCode").isEqualTo("68199")
				.jsonPath("$.value[2].ID").isEqualTo("880147b0-8d2d-4ef8-bb52-ae5ae6002fc5")
				.jsonPath("$.value[2].note").isEqualTo("Don't deliver packages after 5pm")
				.jsonPath("$.value[2].address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[2].address_ID").isEqualTo("100")
				.jsonPath("$.value[2].address.businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[2].address.ID").isEqualTo("100")
				.jsonPath("$.value[2].address.postalCode").isEqualTo("68199");
	}

	@Test
	public void testGetSuppliersWithNotes() throws Exception {
		client.get().uri(addressesURI + "?$expand=notes($orderby=ID)&$filter=businessPartner eq '10401010'").exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.['@context']").isEqualTo("$metadata#Addresses(notes())")
				.jsonPath("$.value[0].ID").isEqualTo("100")
				.jsonPath("$.value[0].postalCode").isEqualTo("68199")
				.jsonPath("$.value[0].notes[0].ID").isEqualTo("83e2643b-aecc-47d3-9f85-a8ba14eff07d")
				.jsonPath("$.value[0].notes[0].note").isEqualTo("Packages can be dropped off at the reception")
				.jsonPath("$.value[0].notes[0].address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[0].notes[0].address_ID").isEqualTo("100")
				.jsonPath("$.value[0].notes[1].ID").isEqualTo("880147b0-8d2d-4ef8-bb52-ae5ae6002fc5")
				.jsonPath("$.value[0].notes[1].note").isEqualTo("Don't deliver packages after 5pm")
				.jsonPath("$.value[0].notes[1].address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[0].notes[1].address_ID").isEqualTo("100")
				.jsonPath("$.value[0].notes[2]").doesNotExist()
				.jsonPath("$.value[1].ID").isEqualTo("200")
				.jsonPath("$.value[1].postalCode").isEqualTo("68789")
				.jsonPath("$.value[2].notes").isEmpty()
				.jsonPath("$.value[2].ID").isEqualTo("300")
				.jsonPath("$.value[2].postalCode").isEqualTo("14469")
				.jsonPath("$.value[2].notes").isEmpty();
	}

	@Test
	public void testGetNotesToSupplier() throws Exception {
		client.get().uri(notesURI + "(ID=5efc842c-c70d-4ee2-af1d-81c7d257aff7,IsActiveEntity=true)/address").exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.['@context']").isEqualTo("$metadata#Addresses/$entity")
				.jsonPath("$.businessPartner").isEqualTo("1000020")
				.jsonPath("$.ID").isEqualTo("500")
				.jsonPath("$.postalCode").isEqualTo("94304");
	}

	@Test
	public void testGetSupplierToNotes() throws Exception {
		client.get().uri(addressesURI + "(businessPartner='10401010',ID='100')/notes").exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.value[0].ID").isEqualTo("83e2643b-aecc-47d3-9f85-a8ba14eff07d")
				.jsonPath("$.value[0].note").isEqualTo("Packages can be dropped off at the reception")
				.jsonPath("$.value[0].address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[0].address_ID").isEqualTo("100")
				.jsonPath("$.value[1].ID").isEqualTo("880147b0-8d2d-4ef8-bb52-ae5ae6002fc5")
				.jsonPath("$.value[1].note").isEqualTo("Don't deliver packages after 5pm")
				.jsonPath("$.value[1].address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.value[1].address_ID").isEqualTo("100")
				.jsonPath("$.value[2]").doesNotExist();
	}

	@Test
	public void testGetSupplierToSpecificNote() throws Exception {
		client.get().uri(addressesURI + "(businessPartner='10401010',ID='100')/notes(ID=83e2643b-aecc-47d3-9f85-a8ba14eff07d,IsActiveEntity=true)").exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.ID").isEqualTo("83e2643b-aecc-47d3-9f85-a8ba14eff07d")
				.jsonPath("$.note").isEqualTo("Packages can be dropped off at the reception")
				.jsonPath("$.address_businessPartner").isEqualTo("10401010")
				.jsonPath("$.address_ID").isEqualTo("100");
	}

	@Test
	public void testGetNotesWithNestedExpands() throws Exception {
		client.get().uri(notesURI + "?$select=note&$expand=address($select=postalCode;$expand=notes($select=note))&$top=1").exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.value[0].ID").isEqualTo("5efc842c-c70d-4ee2-af1d-81c7d257aff7")
				.jsonPath("$.value[0].note").isEqualTo("Ring at building 8")
				.jsonPath("$.value[0].address.businessPartner").isEqualTo("1000020")
				.jsonPath("$.value[0].address.ID").isEqualTo("500")
				.jsonPath("$.value[0].address.postalCode").isEqualTo("94304")
				.jsonPath("$.value[0].address.notes[0].ID").isEqualTo("5efc842c-c70d-4ee2-af1d-81c7d257aff7")
				.jsonPath("$.value[0].address.notes[0].note").isEqualTo("Ring at building 8")
				.jsonPath("$.value[0].address.notes[1]").doesNotExist()
				.jsonPath("$.value[1]").doesNotExist();
	}

	@Test
	public void testGetAddressesWithNestedExpands() throws Exception {
		client.get().uri(addressesURI + "?$select=postalCode&$expand=notes($select=note;$expand=address($select=postalCode))&$filter=businessPartner eq '1000020'").exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.value[0].businessPartner").isEqualTo("1000020")
				.jsonPath("$.value[0].ID").isEqualTo("400")
				.jsonPath("$.value[0].postalCode").isEqualTo("19073")
				.jsonPath("$.value[0].notes").isEmpty()
				.jsonPath("$.value[1].businessPartner").isEqualTo("1000020")
				.jsonPath("$.value[1].ID").isEqualTo("500")
				.jsonPath("$.value[1].postalCode").isEqualTo("94304")
				.jsonPath("$.value[1].notes[0].ID").isEqualTo("5efc842c-c70d-4ee2-af1d-81c7d257aff7")
				.jsonPath("$.value[1].notes[0].note").isEqualTo("Ring at building 8")
				.jsonPath("$.value[1].notes[0].address.businessPartner").isEqualTo("1000020")
				.jsonPath("$.value[1].notes[0].address.ID").isEqualTo("500")
				.jsonPath("$.value[1].notes[0].address.postalCode").isEqualTo("94304")
				.jsonPath("$.value[1].notes[1]").doesNotExist()
				.jsonPath("$.value[2]").doesNotExist();
	}

}
