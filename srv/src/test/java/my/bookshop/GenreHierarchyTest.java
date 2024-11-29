package my.bookshop;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"hybrid","default"})
public class GenreHierarchyTest {

	@Autowired
	private MockMvc client;
	
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
		        + "?$select=DrillState,ID,name"
				+ "&$apply=com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID',Levels=1)"
				+ "&$count=true&$skip=218&$top=20"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].ID").value(10))
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[0].parnt_ID").isEmpty())
				.andExpect(jsonPath("$.value[1].ID").value(20))
				.andExpect(jsonPath("$.value[1].name").value("Non-Fiction"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[1].parnt_ID").isEmpty())
				.andExpect(jsonPath("$.value[2]").doesNotExist());

	}

	@Test
	@WithMockUser(username = "admin")
	void testExpandNonFiction() throws Exception {
		client.perform(get(genresURI
				+ "?$select=DrillState,ID,name"
				+ "&$apply=descendants($root/GenreHierarchy,GenreHierarchy,ID,filter(ID eq 20),1)"
				+ "&$count=true&$skip=0&$top=218"))
				.andExpect(status().isOk())
 				.andExpect(jsonPath("$.value[0].ID").value(21))
				.andExpect(jsonPath("$.value[0].name").value("Biography"))
				// DistanceFromRoot: fix me?
				.andExpect(jsonPath("$.value[0].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[0].DrillState").value("collapsed"))
				.andExpect(jsonPath("$.value[0].parnt_ID").value(20))
				.andExpect(jsonPath("$.value[1].ID").value(23))
				.andExpect(jsonPath("$.value[1].name").value("Essay"))
				.andExpect(jsonPath("$.value[1].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[1].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[1].parnt_ID").value(20))
				.andExpect(jsonPath("$.value[2].ID").value(24))
				.andExpect(jsonPath("$.value[2].name").value("Speech"))
				.andExpect(jsonPath("$.value[2].DistanceFromRoot").value(0))
				.andExpect(jsonPath("$.value[2].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[2].parnt_ID").value(20))
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
		client.perform(get(genresURI
				+ "?$select=DistanceFromRoot,DrillState,ID,LimitedDescendantCount,name"
				+ "&$apply=com.sap.vocabularies.Hierarchy.v1.TopLevels(HierarchyNodes=$root/GenreHierarchy,HierarchyQualifier='GenreHierarchy',NodeProperty='ID')"
				+ "&$count=true&$skip=0&$top=238"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].name").value("Fiction"))
				.andExpect(jsonPath("$.value[0].DrillState").value("expanded"))
				.andExpect(jsonPath("$.value[14].name").value("Speech"))
				.andExpect(jsonPath("$.value[14].DrillState").value("leaf"))
				.andExpect(jsonPath("$.value[15]").doesNotExist());
	}
}
