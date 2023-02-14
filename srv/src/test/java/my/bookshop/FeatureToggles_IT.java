package my.bookshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.sap.cds.reflect.CdsElement;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.request.FeatureTogglesInfo;
import com.sap.cds.services.runtime.CdsRuntime;

import cds.gen.catalogservice.Books_;

// This test case is executable only when MTX sidecar is running.
@ActiveProfiles("ft")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FeatureToggles_IT {

	private static final String ENDPOINT = "/api/browse/Books(%s)";

	@Autowired
	private WebTestClient client;

	@Autowired
	CdsRuntime runtime;

	@Test
	void baseModel_noToggledFeatures() {
		client.get()
			.uri(String.format(ENDPOINT, "4a519e61-3c3a-4bd9-ab12-d7e0c5329933"))
			// This user has all feature toggles disabled
			.headers(httpHeaders -> httpHeaders.setBasicAuth("fred", ""))
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectBody()
			// Elements are not visible and not changed by event handler
				.jsonPath("$.isbn").doesNotExist()
				.jsonPath("$.price").isEqualTo(15);
	}

	@Test
	void modelWithFeatures_allVisible() {
		client.get().uri(String.format(ENDPOINT, "4a519e61-3c3a-4bd9-ab12-d7e0c5329933"))
			// This user has all feature toggles enabled
			.headers(httpHeaders -> httpHeaders.setBasicAuth("erin", ""))
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectBody()
			// Elements are visible and changed by event handler
				.jsonPath("$.isbn").isEqualTo("978-3473523023")
				.jsonPath("$.price").isEqualTo(13.50);
	}

	@Test
	void modifyFeatureTogglesAtRuntime() {
		this.runtime.requestContext()
			.featureToggles(FeatureTogglesInfo.create(Collections.singletonMap("isbn", true))).run(ctx -> {
				CdsModel cdsModel = this.runtime.getCdsModel(ctx.getUserInfo(), ctx.getFeatureTogglesInfo());
				CdsEntity booksEntity = cdsModel.getEntity(Books_.CDS_NAME);

				assertThat(booksEntity.findElement("isbn")).isPresent();
			});
	}
}

