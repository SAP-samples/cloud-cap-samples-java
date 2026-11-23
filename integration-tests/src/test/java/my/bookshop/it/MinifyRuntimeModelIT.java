package my.bookshop.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cds.gen.sap.common.Countries_;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.reflect.CdsDefinitionNotFoundException;
import com.sap.cds.services.runtime.CdsRuntime;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Run with the sidecar started by this module's pre-integration-test phase. */
@ActiveProfiles("local-mtxs")
@AutoConfigureMockMvc
@SpringBootTest(properties = "cds.security.mock.users.admin.tenant=minify-runtime-probe")
class MinifyRuntimeModelIT {

  private static final String TENANT = "minify-runtime-probe";
  private static boolean subscribed;

  @Autowired private CdsRuntime runtime;
  @Autowired private MockMvc client;

  @BeforeAll
  static void subscribe() {
    // The Maven exec plugin starts the sidecar asynchronously.
    await()
        .atMost(Duration.ofSeconds(15))
        .ignoreException(ConnectException.class)
        .untilAsserted(() -> deployment("subscribe"));
    subscribed = true;
  }

  @AfterAll
  static void unsubscribe() throws Exception {
    if (subscribed) {
      deployment("unsubscribe");
    }
  }

  @Test
  void generatedQueryResolvesAgainstLocalModel() {
    var localModel = runtime.getCdsModel();
    assertThat(localModel.findEntity(Countries_.CDS_NAME)).isPresent();
    var query = Select.from(Countries_.class).columns(Countries_::code);
    assertThat(CqnAnalyzer.create(localModel).analyze(query).target().entity().getQualifiedName())
        .isEqualTo(Countries_.CDS_NAME);
  }

  @Test
  void sameGeneratedQueryCannotResolveAgainstTenantModel() {
    var tenantModel = runtime.getCdsModel(TENANT);
    assertThat(tenantModel.findEntity(Countries_.CDS_NAME)).isEmpty();
    var query = Select.from(Countries_.class).columns(Countries_::code);
    assertThatThrownBy(() -> CqnAnalyzer.create(tenantModel).analyze(query))
        .isInstanceOf(CdsDefinitionNotFoundException.class)
        .hasMessageContaining(Countries_.CDS_NAME);
  }

  @Test
  @WithMockUser("admin")
  void javaOnlyPersistenceQueryFailsInTenantRequest() throws Exception {
    client
        .perform(
            post("/api/browse/probeMinifiedCountries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("404002"))
        .andExpect(jsonPath("$.error.message").value("Entity 'sap.common.Countries' not found"));
  }

  private static void deployment(String operation) throws Exception {
    var request =
        HttpRequest.newBuilder(URI.create("http://localhost:4005/-/cds/deployment/" + operation))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"tenant\":\"" + TENANT + "\"}"))
            .build();
    try (var http = HttpClient.newHttpClient()) {
      var response = http.send(request, HttpResponse.BodyHandlers.ofString());
      assertThat(response.statusCode()).describedAs(response.body()).isEqualTo(204);
    }
  }
}
