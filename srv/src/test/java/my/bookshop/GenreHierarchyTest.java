package my.bookshop;

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
				.andExpect(jsonPath("$").value(33));
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
				.andExpect(jsonPath("$.value[0].ID").value("f846b0b9-01d4-4f6d-82a4-d79204f62369"))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].ID").value("d846b0b9-01d4-4f6d-82a4-d79204f62487"))
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
				.andExpect(jsonPath("$.value[1].name").value("Action"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[21].name").value("Speech"))
				.andExpect(jsonPath("$.value[21].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[21].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[22]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testExpandNonFiction() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name"
				+ "&$apply=descendants($root/GenreHierarchy,GenreHierarchy,ID,filter(ID eq d846b0b9-01d4-4f6d-82a4-d79204f62487),1)"
				+ "/orderby(ID)"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Biography"))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].name").value("Essay"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
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
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[32].name").value("Speech"))
				.andExpect(jsonPath("$.value[32].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[33]").doesNotExist());
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
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].name").value("Contemporary Fiction"))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[2].name").value("Fairy Tale"))
				.andExpect(jsonPath("$.value[2].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[2].DistanceFromRoot").value(1))
				.andExpect(jsonPath("$.value[3].name").value("Literary Fiction"))
				.andExpect(jsonPath("$.value[3].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[4].name").value("Mystery"))
				.andExpect(jsonPath("$.value[4].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[5].name").value("Poetry"))
				.andExpect(jsonPath("$.value[5].DrillState").value("leaf"))
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
				.andExpect(jsonPath("$.value[0].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1]").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin")
	void testFilterExpandLevels() throws Exception {
		String expandLevelsJson = """
				[{"NodeID":"f846b0b9-01d4-4f6d-82a4-d79204f62369","Levels":1},{"NodeID":"d846b0b9-01d4-4f6d-82a4-d79204f62487","Levels":1}]\
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
	void testStartTwoLevelsOrderByDescHANA() throws Exception {
		if (isOnHana()) {
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
	}

	private boolean isOnHana() {
		return env.acceptsProfiles(Profiles.of("cloud"));
	}
}