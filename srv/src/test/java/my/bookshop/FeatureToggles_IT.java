package my.bookshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.request.FeatureTogglesInfo;
import com.sap.cds.services.runtime.CdsRuntime;

import cds.gen.catalogservice.Books_;

// This test case is executable only when MTX sidecar is running.
@ActiveProfiles("ft")
@AutoConfigureMockMvc
@SpringBootTest
class FeatureToggles_IT {

	private static final String ENDPOINT = "/api/browse/Books(%s)";

	@Autowired
	private MockMvc client;

	@Autowired
	CdsRuntime runtime;

	@Test
	@WithMockUser("fred") // This user has all feature toggles disabled
	void withoutToggles_basicModelVisible() throws Exception {
		// Elements are not visible and not changed by the event handler
		client.perform(get(String.format(ENDPOINT, "4a519e61-3c3a-4bd9-ab12-d7e0c5329933")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isbn").doesNotExist())
			.andExpect(jsonPath("$.price").value(15));
	}

	@Test
	@WithMockUser("erin") // This user has all feature toggles enabled
	void togglesOn_extensionsAndChangesAreVisible() throws Exception {
		// Elements are visible and changed by the event handler
		client.perform(get(String.format(ENDPOINT, "4a519e61-3c3a-4bd9-ab12-d7e0c5329933")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isbn").value("978-3473523023"))
			.andExpect(jsonPath("$.price").value(13.50));
	}

	@Test
	void featureTogglesModifiedAtRuntime() {
		this.runtime.requestContext()
			.featureToggles(FeatureTogglesInfo.create(Collections.singletonMap("isbn", true))).run(ctx -> {
				CdsModel cdsModel = this.runtime.getCdsModel(ctx.getUserInfo(), ctx.getFeatureTogglesInfo());
				CdsEntity booksEntity = cdsModel.getEntity(Books_.CDS_NAME);

				assertThat(booksEntity.findElement("isbn")).isPresent();
			});
	}
}

