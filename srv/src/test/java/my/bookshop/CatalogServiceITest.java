package my.bookshop;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CatalogServiceITest {

	private static final String booksURI = "/api/browse/Books";

	private static final String USER_USER_STRING = "user";
	private static final String ADMIN_USER_STRING = "admin";

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testDiscountApplied() throws Exception {
		mockMvc.perform(get(booksURI + "?$filter=stock gt 200&top=1")).andExpect(status().isOk())
		.andExpect(jsonPath("$.value[0].title").value(containsString("11% discount")));
	}

	@Test
	public void testDiscountNotApplied() throws Exception {
		mockMvc.perform(get(booksURI + "?$filter=stock lt 100&top=1")).andExpect(status().isOk())
		.andExpect(jsonPath("$.value[0].title").value(not(containsString("11% discount"))));
	}

	@Test
	public void testCreateReviewNotAuthenticated() throws Exception {
		String bookId = "f846b0b9-01d4-4f6d-82a4-d79204f62278";
		Integer rating = 1;
		String title = "title";
		String text = "text";

		String uri = String.format("%s(ID=%s)/CatalogService.addReview", booksURI, bookId);
		String payload = String.format("{ \"rating\": %d, \"title\": \"%s\", \"text\": \"%s\" }", rating, title, text);

		mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(payload))
		.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = USER_USER_STRING)
	public void testCreateReviewByUser() throws Exception {
		String bookId = "f846b0b9-01d4-4f6d-82a4-d79204f62278";
		Integer rating = 1;
		String title = "title";
		String text = "text";

		String uri = String.format("%s(ID=%s)/CatalogService.addReview", booksURI, bookId);
		String payload = String.format("{ \"rating\": %d, \"title\": \"%s\", \"text\": \"%s\" }", rating, title, text);

		mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(payload))
		.andExpect(jsonPath("$.createdBy").value(USER_USER_STRING));
	}

	@Test
	@WithMockUser(username = ADMIN_USER_STRING)
	public void testCreateReviewByAdmin() throws Exception {
		String bookId = "f846b0b9-01d4-4f6d-82a4-d79204f62278";
		Integer rating = 1;
		String title = "title";
		String text = "text";

		String uri = String.format("%s(ID=%s)/CatalogService.addReview", booksURI, bookId);
		String payload = String.format("{ \"rating\": %d, \"title\": \"%s\", \"text\": \"%s\" }", rating, title, text);

		mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(payload))
		.andExpect(jsonPath("$.createdBy").value(ADMIN_USER_STRING));
	}

}
