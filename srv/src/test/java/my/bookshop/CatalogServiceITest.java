package my.bookshop;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CatalogServiceITest {

	private static final String booksURI = "/api/browse/Books";

	@Autowired
	private MockMvc mockMvc;

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
