package my.bookshop;

import static cds.gen.catalogservice.CatalogService_.REVIEWS;
import static org.assertj.core.api.Assertions.assertThat;

import cds.gen.catalogservice.Reviews;
import com.sap.cds.ql.Delete;
import com.sap.cds.services.persistence.PersistenceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest
@AutoConfigureRestTestClient
class CatalogServiceITest {

  private static final String booksURI = "/api/browse/Books";
  private static final String addReviewURI =
      "%s(ID=%s)/CatalogService.addReview"
          .formatted(booksURI, "f846b0b9-01d4-4f6d-82a4-d79204f62278");

  private static final String USER_USER_STRING = "user";
  private static final String ADMIN_USER_STRING = "admin";

  @Autowired private RestTestClient client;

  @Autowired private PersistenceService db;

  @AfterEach
  void cleanup() {
    db.run(Delete.from(REVIEWS));
  }

  @Test
  void discountApplied() {
    client
        .get()
        .uri(booksURI + "?$filter=stock gt 200&top=1")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].title")
        .value(String.class, title -> assertThat(title).contains("11% discount"));
  }

  @Test
  void discountNotApplied() {
    client
        .get()
        .uri(booksURI + "?$filter=stock lt 100&top=1")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].title")
        .value(String.class, title -> assertThat(title).doesNotContain("11% discount"));
  }

  @Test
  void createReviewNotAuthenticated() {
    String payload = createTestReview().toJson();
    client
        .post()
        .uri(addReviewURI)
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @WithMockUser(USER_USER_STRING)
  void createReviewByUser() {
    String payload = createTestReview().toJson();
    client
        .post()
        .uri(addReviewURI)
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.createdBy")
        .isEqualTo(USER_USER_STRING);
  }

  @Test
  @WithMockUser(ADMIN_USER_STRING)
  void createReviewByAdmin() {
    String payload = createTestReview().toJson();
    client
        .post()
        .uri(addReviewURI)
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.createdBy")
        .isEqualTo(ADMIN_USER_STRING);
  }

  private Reviews createTestReview() {
    Reviews review = Reviews.create();
    review.setRating(1);
    review.setTitle("title");
    review.setText("text");
    return review;
  }
}
