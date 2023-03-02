package my.bookshop;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// This test case is executable only when MTX sidecar is running.
@ActiveProfiles({"default", "ft"})
@AutoConfigureMockMvc
@SpringBootTest
class FeatureTogglesIT {

	private static final String ENDPOINT = "/api/browse/Books(aebdfc8a-0dfa-4468-bd36-48aabd65e663)";

	@Autowired
	private MockMvc client;

	@Test
	@WithMockUser("authenticated") // This user has all feature toggles disabled
	void withoutToggles_basicModelVisible() throws Exception {
		// Elements are not visible and not changed by the event handler
		client.perform(get(ENDPOINT))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isbn").doesNotExist())
			.andExpect(jsonPath("$.title").value(containsString("11%")));
	}

	@Test
	@WithMockUser("admin") // This user has all feature toggles enabled
	void togglesOn_extensionsAndChangesAreVisible() throws Exception {
		// Elements are visible and changed by the event handler
		client.perform(get(ENDPOINT))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isbn").value("979-8669820985"))
			.andExpect(jsonPath("$.title").value(containsString("14%")));
	}

	@Test
	@WithMockUser("user") // This user has only 'isbn' toggle enabled
	void toggleIsbnOn_extensionsAndChangesAreVisible() throws Exception {
		// Elements are visible
		client.perform(get(ENDPOINT))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isbn").value("979-8669820985"))
			.andExpect(jsonPath("$.title").value(containsString("11%")));
	}

}

