package my.bookshop;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@AutoConfigureRestTestClient
class GenreHierarchyTest {

  @Autowired private RestTestClient client;

  private static final String genresURI = "/api/browse/GenreHierarchy";

  @Test
  @WithMockUser(username = "admin")
  void getAll() {
    client.get().uri(genresURI).exchange().expectStatus().isOk();
  }

  @Test
  @WithMockUser(username = "admin")
  void countAll() {
    client
        .get()
        .uri(genresURI + "/$count")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isEqualTo(269);
  }

  @Test
  @WithMockUser(username = "admin")
  void startOneLevel() {
    client
        .get()
        .uri(
            genresURI
                + "?$select=DrillState,ID,name,DistanceFromRoot"
                + "&$apply=orderby(name)/"
                + "com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchyHierarchy',NodeProperty='ID',Levels=1)"
                + "&$count=true")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].ID")
        .isEqualTo("8bbf14c6-b378-4e35-9b4f-05a9c8878001")
        .jsonPath("$.value[0].name")
        .isEqualTo("Fiction")
        .jsonPath("$.value[0].DistanceFromRoot")
        .isEqualTo(0)
        .jsonPath("$.value[0].DrillState")
        .isEqualTo("collapsed")
        .jsonPath("$.value[1].ID")
        .isEqualTo("8bbf14c6-b378-4e35-9b4f-05a9c8878002")
        .jsonPath("$.value[1].name")
        .isEqualTo("Non-Fiction")
        .jsonPath("$.value[1].DistanceFromRoot")
        .isEqualTo(0)
        .jsonPath("$.value[1].DrillState")
        .isEqualTo("collapsed")
        .jsonPath("$.value[2]")
        .doesNotExist();
  }

  @Test
  @WithMockUser(username = "admin")
  void startTwoLevels() {
    client
        .get()
        .uri(
            genresURI
                + "?$select=DrillState,ID,name,DistanceFromRoot"
                + "&$apply=orderby(name)/"
                + "com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchyHierarchy',NodeProperty='ID',Levels=2)"
                + "&$count=true")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].name")
        .isEqualTo("Fiction")
        .jsonPath("$.value[0].DrillState")
        .isEqualTo("expanded")
        .jsonPath("$.value[0].DistanceFromRoot")
        .isEqualTo(0)
        .jsonPath("$.value[1].name")
        .isEqualTo("Action & Adventure")
        .jsonPath("$.value[1].DrillState")
        .isEqualTo("leaf")
        .jsonPath("$.value[1].DistanceFromRoot")
        .isEqualTo(1)
        .jsonPath("$.value[182].name")
        .isEqualTo("True Crime")
        .jsonPath("$.value[182].DrillState")
        .isEqualTo("leaf")
        .jsonPath("$.value[182].DistanceFromRoot")
        .isEqualTo(1)
        .jsonPath("$.value[183]")
        .doesNotExist();
  }

  @Test
  @WithMockUser(username = "admin")
  void expandNonFiction() {
    client
        .get()
        .uri(
            genresURI
                + "?$select=DrillState,ID,name"
                + "&$apply=descendants($root/GenreHierarchy,GenreHierarchyHierarchy,ID,filter(ID eq 8bbf14c6-b378-4e35-9b4f-05a9c8878021),1)"
                + "/orderby(ID)")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].name")
        .isEqualTo("Detective Fiction")
        .jsonPath("$.value[0].DrillState")
        .isEqualTo("leaf")
        .jsonPath("$.value[1]")
        .doesNotExist();
  }

  @Test
  @WithMockUser(username = "admin")
  void collapseAll() {
    client
        .get()
        .uri(
            genresURI
                + "?$select=DrillState,ID,name"
                + "&$apply=orderby(name)/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchyHierarchy',NodeProperty='ID',Levels=1)"
                + "&$count=true&$skip=0&$top=238")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].name")
        .isEqualTo("Fiction")
        .jsonPath("$.value[0].DrillState")
        .isEqualTo("collapsed")
        .jsonPath("$.value[1].name")
        .isEqualTo("Non-Fiction")
        .jsonPath("$.value[1].DrillState")
        .isEqualTo("collapsed")
        .jsonPath("$.value[2]")
        .doesNotExist();
  }

  @Test
  @WithMockUser(username = "admin")
  void expandAllTop100() {
    String url =
        genresURI
            + "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
            + "&$apply=orderby(name)/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchyHierarchy',NodeProperty='ID')"
            + "&$count=true&$skip=0&$top=100";

    client
        .get()
        .uri(url)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].name")
        .isEqualTo("Fiction")
        .jsonPath("$.value[0].DrillState")
        .isEqualTo("expanded")
        .jsonPath("$.value[0].DistanceFromRoot")
        .isEqualTo(0)
        .jsonPath("$.value[99].name")
        .isEqualTo("New Weird")
        .jsonPath("$.value[99].DrillState")
        .isEqualTo("leaf")
        .jsonPath("$.value[100]")
        .doesNotExist();
  }

  @Test
  @WithMockUser(username = "admin")
  void search() {
    client
        .get()
        .uri(
            genresURI
                + "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
                + "&$apply=ancestors($root/GenreHierarchy,GenreHierarchyHierarchy,ID,search(\"true\"),keep start)"
                + "/orderby(name)"
                + "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchyHierarchy',NodeProperty='ID')"
                + "&$count=true")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].name")
        .isEqualTo("Fiction")
        .jsonPath("$.value[0].DrillState")
        .isEqualTo("expanded")
        .jsonPath("$.value[0].DistanceFromRoot")
        .isEqualTo(0)
        .jsonPath("$.value[1].name")
        .isEqualTo("Adventure")
        .jsonPath("$.value[1].DrillState")
        .isEqualTo("expanded")
        .jsonPath("$.value[1].DistanceFromRoot")
        .isEqualTo(1)
        .jsonPath("$.value[2].name")
        .isEqualTo("True Adventure")
        .jsonPath("$.value[2].DrillState")
        .isEqualTo("leaf")
        .jsonPath("$.value[2].DistanceFromRoot")
        .isEqualTo(2)
        .jsonPath("$.value[3].name")
        .isEqualTo("Non-Fiction")
        .jsonPath("$.value[3].DrillState")
        .isEqualTo("expanded")
        .jsonPath("$.value[3].DistanceFromRoot")
        .isEqualTo(0)
        .jsonPath("$.value[4].name")
        .isEqualTo("True Crime")
        .jsonPath("$.value[4].DrillState")
        .isEqualTo("leaf")
        .jsonPath("$.value[4].DistanceFromRoot")
        .isEqualTo(1)
        .jsonPath("$.value[5]")
        .doesNotExist();
  }

  @Test
  @WithMockUser(username = "admin")
  void filterNotExpanded() {
    client
        .get()
        .uri(
            genresURI
                + "?$select=DrillState,ID,name,DistanceFromRoot"
                + "&$apply=ancestors($root/GenreHierarchy,GenreHierarchyHierarchy,ID,filter(name eq 'Autobiography'),keep start)/orderby(name)"
                + "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchyHierarchy',NodeProperty='ID',Levels=1)")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].name")
        .isEqualTo("Non-Fiction")
        .jsonPath("$.value[0].DrillState")
        .isEqualTo("collapsed")
        .jsonPath("$.value[0].DistanceFromRoot")
        .isEqualTo(0)
        .jsonPath("$.value[1]")
        .doesNotExist();
  }

  @Test
  @WithMockUser(username = "admin")
  void filterExpandLevels() {
    String expandLevelsJson =
        """
				[{"NodeID":"8bbf14c6-b378-4e35-9b4f-05a9c8878002","Levels":1},{"NodeID":"8bbf14c6-b378-4e35-9b4f-05a9c8878031","Levels":1}]\
				""";
    String unencoded =
        genresURI
            + "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
            + "&$apply=ancestors($root/GenreHierarchy,GenreHierarchyHierarchy,ID,filter(name eq 'Autobiography'),keep start)/orderby(name)"
            + "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchyHierarchy',NodeProperty='ID',Levels=1,ExpandLevels="
            + expandLevelsJson
            + ")&$count=true";
    String uriString = UriComponentsBuilder.fromUriString(unencoded).toUriString();
    URI uri = URI.create(uriString);
    client
        .get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].name")
        .isEqualTo("Non-Fiction")
        .jsonPath("$.value[0].DrillState")
        .isEqualTo("expanded")
        .jsonPath("$.value[0].DistanceFromRoot")
        .isEqualTo(0)
        .jsonPath("$.value[2]")
        .doesNotExist();
  }

  @Test
  @WithMockUser(username = "admin")
  void startTwoLevelsOrderByDesc() {
    client
        .get()
        .uri(
            genresURI
                + "?$select=DrillState,ID,name,DistanceFromRoot"
                + "&$apply=orderby(name desc)/"
                + "com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchyHierarchy',NodeProperty='ID',Levels=2)"
                + "&$count=true")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.value[0].name")
        .isEqualTo("Non-Fiction")
        .jsonPath("$.value[1].name")
        .isEqualTo("True Crime")
        .jsonPath("$.value[182].name")
        .isEqualTo("Action & Adventure")
        .jsonPath("$.value[183]")
        .doesNotExist();
  }
}
