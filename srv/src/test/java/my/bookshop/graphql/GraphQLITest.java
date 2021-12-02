package my.bookshop.graphql;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GraphQLITest {

	private static final String graphqlURI = "/graphql";

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser("user")
	public void testBasicQuery() throws Exception {
		String query = "{ \"query\": \"{ getCatalogServiceBooksById(ID: \\\"f846b0b9-01d4-4f6d-82a4-d79204f62278\\\") { ID title } }\" }";
		MvcResult result = mockMvc.perform(post(graphqlURI).contentType(MediaType.APPLICATION_JSON).content(query)).andReturn();
		mockMvc.perform(asyncDispatch(result))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.ID").value("f846b0b9-01d4-4f6d-82a4-d79204f62278"))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.title").value("Wuthering Heights"));
	}

	@Test
	@WithMockUser("user")
	public void testLocalized() throws Exception {
		String query = "{ \"query\": \"{ getCatalogServiceBooksById(ID: \\\"f846b0b9-01d4-4f6d-82a4-d79204f62278\\\") { ID title descr stock price createdAt } }\" }";
		MvcResult result = mockMvc.perform(post(graphqlURI).contentType(MediaType.APPLICATION_JSON).locale(Locale.GERMAN).content(query)).andReturn();
		mockMvc.perform(asyncDispatch(result))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.ID").value("f846b0b9-01d4-4f6d-82a4-d79204f62278"))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.title").value("Sturmhöhe"))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.descr").value(containsString("Sturmhöhe")))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.stock").value(12))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.price").value(11.11))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.createdAt").exists());
	}

	@Test
	@WithMockUser("user")
	public void testBusinessLogicTriggered() throws Exception {
		String query = "{ \"query\": \"{ getCatalogServiceBooksById(ID: \\\"51061ce3-ddde-4d70-a2dc-6314afbcc73e\\\") { ID title stock price } }\" }";
		MvcResult result = mockMvc.perform(post(graphqlURI).contentType(MediaType.APPLICATION_JSON).content(query)).andReturn();
		mockMvc.perform(asyncDispatch(result))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.ID").value("51061ce3-ddde-4d70-a2dc-6314afbcc73e"))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.title").value("The Raven -- 11% discount"))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.stock").value(333))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.price").value(13.13));
	}

	@Test
	@WithMockUser("user")
	public void testStructuredQueries() throws Exception {
		String query = "{ \"query\": \"{ getCatalogServiceBooksById(ID: \\\"f846b0b9-01d4-4f6d-82a4-d79204f62278\\\") { ID price author { name } currency { code } } }\" }";
		MvcResult result = mockMvc.perform(post(graphqlURI).contentType(MediaType.APPLICATION_JSON).content(query)).andReturn();
		mockMvc.perform(asyncDispatch(result))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.ID").value("f846b0b9-01d4-4f6d-82a4-d79204f62278"))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.price").value(11.11))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.author.name").value(containsString("Emily")))
				.andExpect(jsonPath("$.data.getCatalogServiceBooksById.currency.code").value("GBP"));
	}

	@Test
	@WithMockUser("user")
	public void testCollectionQuery() throws Exception {
		String query = "{ \"query\": \"{ getCatalogServiceBooks { ID price currency { code } } }\" }";
		MvcResult result = mockMvc.perform(post(graphqlURI).contentType(MediaType.APPLICATION_JSON).content(query)).andReturn();
		mockMvc.perform(asyncDispatch(result))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.getCatalogServiceBooks").isArray())
				.andExpect(jsonPath("$.data.getCatalogServiceBooks[0].ID").value("f846b0b9-01d4-4f6d-82a4-d79204f62278"))
				.andExpect(jsonPath("$.data.getCatalogServiceBooks[0].price").value(11.11))
				.andExpect(jsonPath("$.data.getCatalogServiceBooks[0].currency.code").value("GBP"))
				.andExpect(jsonPath("$.data.getCatalogServiceBooks[4]").exists())
				.andExpect(jsonPath("$.data.getCatalogServiceBooks[5]").doesNotExist());
	}

	@Test
	@WithMockUser("user")
	public void testAuthorizationChecks() throws Exception {
		String query = "{ \"query\": \"{ getAdminServiceOrdersById(IsActiveEntity: true, ID: \\\"7e2f2640-6866-4dcf-8f4d-3027aa831cad\\\") { ID Items { quantity book { title } } } }\" }";
		MvcResult result = mockMvc.perform(post(graphqlURI).contentType(MediaType.APPLICATION_JSON).content(query)).andReturn();
		mockMvc.perform(asyncDispatch(result))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.errors.[0].message").value(containsString("Not authorized")));
	}

	@Test
	@WithMockUser("admin")
	public void testDeepQuery() throws Exception {
		String query = "{ \"query\": \"{ getAdminServiceOrdersById(IsActiveEntity: true, ID: \\\"7e2f2640-6866-4dcf-8f4d-3027aa831cad\\\") { ID Items { quantity book { title } } } }\" }";
		MvcResult result = mockMvc.perform(post(graphqlURI).contentType(MediaType.APPLICATION_JSON).content(query)).andReturn();
		mockMvc.perform(asyncDispatch(result))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.getAdminServiceOrdersById.ID").value("7e2f2640-6866-4dcf-8f4d-3027aa831cad"))
				.andExpect(jsonPath("$.data.getAdminServiceOrdersById.Items").isArray())
				.andExpect(jsonPath("$.data.getAdminServiceOrdersById.Items[0].quantity").value(1))
				.andExpect(jsonPath("$.data.getAdminServiceOrdersById.Items[0].book.title").value("Wuthering Heights"))
				.andExpect(jsonPath("$.data.getAdminServiceOrdersById.Items[1].quantity").value(1))
				.andExpect(jsonPath("$.data.getAdminServiceOrdersById.Items[1].book.title").value("Catweazle"))
				.andExpect(jsonPath("$.data.getAdminServiceOrdersById.Items[2]").doesNotExist());
	}

}
