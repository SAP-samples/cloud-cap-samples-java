package my.bookshop;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.sap.cds.services.persistence.PersistenceService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CatalogServiceITest {

	private static final String booksURI = "/api/browse/Books";
	private static final String addReviewURI = String.format("%s(ID=%s)/CatalogService.addReview", booksURI, "f846b0b9-01d4-4f6d-82a4-d79204f62278");

	private static final String USER_USER_STRING = "user";
	private static final String ADMIN_USER_STRING = "admin";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PersistenceService db;

	@Test
	public void testDiscountApplied() throws Exception {
		mockMvc.perform(get(booksURI + "?$filter=stock gt 200&top=1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.value[0].title").value(containsString("11% discount")));
	}

	@Test
	public void testDiscountNotApplied() throws Exception {
		mockMvc.perform(get(booksURI + "?$filter=stock lt 100&top=1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.value[0].title").value(not(containsString("11% discount"))));
	}
}
