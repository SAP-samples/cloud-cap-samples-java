package my.bookshop.it;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

// This test case is executable only when MTX sidecar is running.
@ActiveProfiles({"default", "ft"})
@AutoConfigureRestTestClient
@SpringBootTest
class FeatureTogglesIT {

  private static final String ENDPOINT = "/api/browse/Books(aebdfc8a-0dfa-4468-bd36-48aabd65e663)";

  @Autowired private RestTestClient client;

  @Test
  @WithMockUser("authenticated") // This user has all feature toggles disabled
  void withoutToggles_basicModelVisible() {
    // Elements are not visible and not changed by the event handler
    client
        .get()
        .uri(ENDPOINT)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.isbn")
        .doesNotExist()
        .jsonPath("$.title")
        .value(String.class, title -> assertThat(title).contains("11%"));
  }

  @Test
  @WithMockUser("admin") // This user has all feature toggles enabled
  void togglesOn_extensionsAndChangesAreVisible() {
    // Elements are visible and changed by the event handler
    client
        .get()
        .uri(ENDPOINT)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.isbn")
        .isEqualTo("979-8669820985")
        .jsonPath("$.title")
        .value(String.class, title -> assertThat(title).contains("14%"));
  }

  @Test
  @WithMockUser("user") // This user has only 'isbn' toggle enabled
  void toggleIsbnOn_extensionsAndChangesAreVisible() {
    // Elements are visible
    client
        .get()
        .uri(ENDPOINT)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.isbn")
        .isEqualTo("979-8669820985")
        .jsonPath("$.title")
        .value(String.class, title -> assertThat(title).contains("11%"));
  }
}
