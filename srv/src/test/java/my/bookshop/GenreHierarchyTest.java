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
				.andExpect(jsonPath("$").value(30));
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
				.andExpect(jsonPath("$.value[0].ID").value(100))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].ID").value(200))
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
				.andExpect(jsonPath("$.value[0].ID").value(100))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].ID").value(101))
				.andExpect(jsonPath("$.value[1].name").value("Action"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[20].ID").value(204))
				.andExpect(jsonPath("$.value[20].name").value("Speech"))
				.andExpect(jsonPath("$.value[20].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[20].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[21]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testExpandNonFiction() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name"
				+ "&$apply=descendants($root/GenreHierarchy,GenreHierarchy,ID,filter(ID eq 200),1)"
				+ "/orderby(ID)"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(201))
				.andExpect(jsonPath("$.value[0].name").value("Biography"))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].ID").value(203))
				.andExpect(jsonPath("$.value[1].name").value("Essay"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[2].ID").value(204))
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
				.andExpect(jsonPath("$.value[0].ID").value(100))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[29].name").value("Speech"))
				.andExpect(jsonPath("$.value[29].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[30]").doesNotExist());
		if (isOnHana()) {
			expectations.andExpect(jsonPath("$.value[0].LimitedDescendantCount").value(24));
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
				.andExpect(jsonPath("$.value[0].ID").value(100))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].ID").value(120))
				.andExpect(jsonPath("$.value[1].name").value("Contemporary Fiction"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[2].ID").value(128))
				.andExpect(jsonPath("$.value[2].name").value("Fairy Tale"))
				.andExpect(jsonPath("$.value[2].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[2].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[3].ID").value(122))
				.andExpect(jsonPath("$.value[6].name").value("Short Story"))
				.andExpect(jsonPath("$.value[6].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[6].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[7]").doesNotExist())
				;
		if (isOnHana()) {
			expectations.andExpect(jsonPath("$.value[0].LimitedDescendantCount").value(6))
					.andExpect(jsonPath("$.value[1].LimitedDescendantCount").value(0))
					.andExpect(jsonPath("$.value[2].LimitedDescendantCount").value(0))
					.andExpect(jsonPath("$.value[6].LimitedDescendantCount").value(0));
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
				.andExpect(jsonPath("$.value[0].ID").value(200))
				.andExpect(jsonPath("$.value[0].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testFilterExpandLevels() throws Exception {
		String expandLevelsJson = """
				[{"NodeID":100,"Levels":1},{"NodeID":200,"Levels":1}]\
				""";
		String unencoded = genresURI + "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
				+ "&$apply=ancestors($root/GenreHierarchy,GenreHierarchy,ID,filter(name eq 'Autobiography'),keep start)/orderby(name)"
				+ "/com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1,ExpandLevels="
				+ expandLevelsJson + ")&$count=true";
		String uriString = UriComponentsBuilder.fromUriString(unencoded).toUriString();
		URI uri = URI.create(uriString);
		client.perform(get(uri))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(200))
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
				.andExpect(jsonPath("$.value[0].ID").value(200))
				.andExpect(jsonPath("$.value[1].ID").value(204))
				.andExpect(jsonPath("$.value[20].ID").value(101))
				.andExpect(jsonPath("$.value[21]").doesNotExist());
	}

	private boolean isOnHana() {
		return env.acceptsProfiles(Profiles.of("hybrid"));
	}
}