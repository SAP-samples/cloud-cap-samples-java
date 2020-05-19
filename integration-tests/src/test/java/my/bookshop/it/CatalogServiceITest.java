package my.bookshop.it;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import my.bookshop.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class CatalogServiceITest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testReadBooks() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/browse/Books")).andExpect(status().isOk())
				.andExpect(jsonPath("$.value[0].title").value(containsString("Wuthering Heights")))
				.andExpect(jsonPath("$.value[0].stock").value(12))
				.andExpect(jsonPath("$.value[1].title").value(containsString("Jane Eyre")))
				.andExpect(jsonPath("$.value[1].stock").value(11));
	}
}
