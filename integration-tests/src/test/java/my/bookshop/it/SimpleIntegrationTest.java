package my.bookshop.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import my.bookshop.Application;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Testcontainers
class SimpleIntegrationTest {

	@Autowired
	MockMvc client;

	@Container
	private static PostgreSQLContainer<?> pgContainer = new PostgreSQLContainer<>("postgres:14");

	@DynamicPropertySource
	static void registerMySQLProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", pgContainer::getJdbcUrl);
		registry.add("spring.datasource.username", pgContainer::getUsername);
		registry.add("spring.datasource.password", pgContainer::getPassword);
	}

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
