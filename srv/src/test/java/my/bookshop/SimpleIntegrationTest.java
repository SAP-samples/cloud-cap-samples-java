package my.bookshop;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SimpleIntegrationTest {

	@Autowired
	MockMvc client;

	@Test
	void booksAreReadable() throws Exception {
		client.perform(get("/api/browse/Books").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser("admin")
	void booksAreManageable() throws Exception {
		client.perform(get("/api/admin/Books").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void booksAreProtected() throws Exception {
		client.perform(get("/api/admin/Books").accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
	}
}
