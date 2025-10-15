package my.bookshop;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class GenreHierarchyTest {

	@Autowired
	private MockMvc client;

	private static final String genresURI = "/api/browse/GenreHierarchy";

	@Test
	@WithMockUser(username = "admin")
	void testGetAll() throws Exception {
		client.perform(get(genresURI)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin")
	void testCountAll() throws Exception {
		client.perform(get(genresURI + "/$count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(269));
	}

	@Test
	@WithMockUser(username = "admin")
	void testStartOneLevel() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name,DistanceFromRoot"
				+ "&$apply=orderby(name)/"
				+ "com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1)"
				+ "&$count=true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value("8bbf14c6-b378-4e35-9b4f-05a9c8878001"))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].ID").value("8bbf14c6-b378-4e35-9b4f-05a9c8878002"))
				.andExpect(jsonPath("$.value[1].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[2]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testStartTwoLevels() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name,DistanceFromRoot"
				+ "&$apply=orderby(name)/"
				+ "com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=2)"
				+ "&$count=true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].name").value("Action & Adventure"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[182].name").value("True Crime"))
				.andExpect(jsonPath("$.value[182].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[182].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[183]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testExpandNonFiction() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name"
				+ "&$apply=descendants($root/GenreHierarchy,GenreHierarchy,ID,filter(ID eq 8bbf14c6-b378-4e35-9b4f-05a9c8878021),1)"
				+ "/orderby(ID)"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Detective Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testCollapseAll() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name"
				+ "&$apply=orderby(name)/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1)"
				+ "&$count=true&$skip=0&$top=238"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[1].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[2]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testExpandAllTop100() throws Exception {
		String url = genresURI
				+ "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
				+ "&$apply=orderby(name)/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID')"
				+ "&$count=true&$skip=0&$top=100";

		client.perform(get(url))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[99].name").value("New Weird"))
				.andExpect(jsonPath("$.value[99].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[100]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testSearch() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
				+ "&$apply=ancestors($root/GenreHierarchy,GenreHierarchy,ID,search(\"true\"),keep start)"
				+ "/orderby(name)"
				+ "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID')"
				+ "&$count=true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].name").value("Adventure"))
				.andExpect(jsonPath("$.value[1].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[2].name").value("True Adventure"))
				.andExpect(jsonPath("$.value[2].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[2].DistanceFromRoot").value(2))
				.andExpect(jsonPath("$.value[3].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[3].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[3].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[4].name").value("True Crime"))
				.andExpect(jsonPath("$.value[4].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[4].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[5]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testFilterNotExpanded() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name,DistanceFromRoot"
				+ "&$apply=ancestors($root/GenreHierarchy,GenreHierarchy,ID,filter(name eq 'Autobiography'),keep start)/orderby(name)"
				+ "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1)"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testFilterExpandLevels() throws Exception {
		String expandLevelsJson = """
				[{"NodeID":"8bbf14c6-b378-4e35-9b4f-05a9c8878002","Levels":1},{"NodeID":"8bbf14c6-b378-4e35-9b4f-05a9c8878031","Levels":1}]\
				""";
		String unencoded = genresURI + "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
				+ "&$apply=ancestors($root/GenreHierarchy,GenreHierarchy,ID,filter(name eq 'Autobiography'),keep start)/orderby(name)"
				+ "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1,ExpandLevels="
				+ expandLevelsJson + ")&$count=true";
		String uriString = UriComponentsBuilder.fromUriString(unencoded).toUriString();
		URI uri = URI.create(uriString);
		client.perform(get(uri))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[2]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testStartTwoLevelsOrderByDesc() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name,DistanceFromRoot"
				+ "&$apply=orderby(name desc)/"
				+ "com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=2)"
				+ "&$count=true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[1].name").value("True Crime"))
				.andExpect(jsonPath("$.value[182].name").value("Action & Adventure"))
				.andExpect(jsonPath("$.value[183]").doesNotExist());
	}
}
