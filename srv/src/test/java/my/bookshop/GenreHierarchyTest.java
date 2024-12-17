package my.bookshop;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class GenreHierarchyTest {

	@Autowired
	private MockMvc client;

	@Autowired
	Environment env;

	private static final String genresURI = "/api/admin/GenreHierarchy";

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
				.andExpect(jsonPath("$").value(15));
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
				.andExpect(jsonPath("$.value[0].ID").value(10))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].ID").value(20))
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
				.andExpect(jsonPath("$.value[0].ID").value(10))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].ID").value(11))
				.andExpect(jsonPath("$.value[1].name").value("Drama"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[11].ID").value(21))
				.andExpect(jsonPath("$.value[11].name").value("Biography"))
				.andExpect(jsonPath("$.value[11].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[11].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[14]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testExpandNonFiction() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name"
				+ "&$apply=descendants($root/GenreHierarchy,GenreHierarchy,ID,filter(ID eq 20),1)"
				+ "/orderby(ID)"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(21))
				.andExpect(jsonPath("$.value[0].name").value("Biography"))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].ID").value(23))
				.andExpect(jsonPath("$.value[1].name").value("Essay"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[2].ID").value(24))
				.andExpect(jsonPath("$.value[2].name").value("Speech"))
				.andExpect(jsonPath("$.value[2].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[3]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testCollapseAll() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name"
				+ "&$apply=com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1)"
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
	void testExpandAll() throws Exception {
		String url = genresURI
				+ "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
				+ "&$apply=com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID')"
				+ "&$count=true&$skip=0&$top=238";

		ResultActions expectations = client.perform(get(url))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(10))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[14].name").value("Speech"))
				.andExpect(jsonPath("$.value[14].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[15]").doesNotExist());
		if (isOnHana()) {
			expectations.andExpect(jsonPath("$.value[0].LimitedDescendantCount").value(9));
		}
	}

	@Test
	@WithMockUser(username = "admin")
	void testSearch() throws Exception {
		ResultActions expectations = client.perform(get(genresURI
				+ "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
				+ "&$apply=ancestors($root/GenreHierarchy,GenreHierarchy,ID,search(\"ry\"),keep start)"
				+ "/orderby(name)"
				+ "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID')"
				+ "&$count=true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(10))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].ID").value(19))
				.andExpect(jsonPath("$.value[1].name").value("Fairy Tale"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[2].ID").value(16))
				.andExpect(jsonPath("$.value[2].name").value("Mystery"))
				.andExpect(jsonPath("$.value[2].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[2].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[3].ID").value(12))
				.andExpect(jsonPath("$.value[3].name").value("Poetry"))
				.andExpect(jsonPath("$.value[3].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[3].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[4]").doesNotExist());
		if (isOnHana()) {
			expectations.andExpect(jsonPath("$.value[0].LimitedDescendantCount").value(3))
					.andExpect(jsonPath("$.value[1].LimitedDescendantCount").value(0))
					.andExpect(jsonPath("$.value[2].LimitedDescendantCount").value(0))
					.andExpect(jsonPath("$.value[3].LimitedDescendantCount").value(0));
		}
	}

	@Test
	@WithMockUser(username = "admin")
	void testFilterNotExpanded() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name,DistanceFromRoot"
				+ "&$apply=ancestors($root/GenreHierarchy,GenreHierarchy,ID,filter(name eq 'Autobiography'),keep start)/orderby(name)"
				+ "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1)"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(20))
				.andExpect(jsonPath("$.value[0].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testFilterExpandLevels() throws Exception {
		String expandLevelsJson = """
				[{"NodeID":10,"Levels":1},{"NodeID":20,"Levels":1}]\
				""";
		String unencoded = genresURI + "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
				+ "&$apply=ancestors($root/GenreHierarchy,GenreHierarchy,ID,filter(name eq 'Autobiography'),keep start)/orderby(name)"
				+ "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1,ExpandLevels="
				+ expandLevelsJson + ")&$count=true";
		String uriString = UriComponentsBuilder.fromUriString(unencoded).toUriString();
		URI uri = URI.create(uriString);
		client.perform(get(uri))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(20))
				.andExpect(jsonPath("$.value[0].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[2]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testStartTwoLevelsOrderByDescHANA() throws Exception {
		assumeThat(env.getActiveProfiles()).contains("hybrid");
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name,DistanceFromRoot"
				+ "&$apply=orderby(name desc)/"
				+ "com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=2)"
				+ "&$count=true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(20))
				.andExpect(jsonPath("$.value[0].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].ID").value(24))
				.andExpect(jsonPath("$.value[1].name").value("Speech"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[3].ID").value(21))
				.andExpect(jsonPath("$.value[3].name").value("Biography"))
				.andExpect(jsonPath("$.value[3].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[3].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[14]").doesNotExist());
	}

	private boolean isOnHana() {
		return env.acceptsProfiles(Profiles.of("hybrid"));
	}
}