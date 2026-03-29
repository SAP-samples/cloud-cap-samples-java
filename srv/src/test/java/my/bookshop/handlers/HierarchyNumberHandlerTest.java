package my.bookshop.handlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for {@link HierarchyNumberHandler} verifying the computed {@code number}
 * element on GenreHierarchy.
 *
 * <p>Test data (from CSV, contiguous 1-based siblingRank per parent group):
 *
 * <ul>
 *   <li>Fiction (root, siblingRank=1) -> number "1"
 *   <li>Non-Fiction (root, siblingRank=2) -> number "2"
 *   <li>Memoir (child of Non-Fiction, siblingRank=20) -> number "2.20"
 *   <li>Cooking Memoir (child of Memoir, siblingRank=1) -> number "2.20.1"
 *   <li>War Memoir (child of Memoir, siblingRank=5) -> number "2.20.5"
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class HierarchyNumberHandlerTest {

  @Autowired private MockMvc mockMvc;

  private static final String FICTION_ID = "8bbf14c6-b378-4e35-9b4f-05a9c8878001";
  private static final String NON_FICTION_ID = "8bbf14c6-b378-4e35-9b4f-05a9c8878002";
  private static final String MEMOIR_ID = "8bbf14c6-b378-4e35-9b4f-05a9c8878059";
  private static final String COOKING_MEMOIR_ID = "8bbf14c6-b378-4e35-9b4f-05a9c8878150";
  private static final String WAR_MEMOIR_ID = "8bbf14c6-b378-4e35-9b4f-05a9c8878270";

  private static final String SINGLE_ENTITY_URI =
      "/api/admin/GenreHierarchy(ID=%s,IsActiveEntity=true)?$select=number";

  @Test
  void rootNodeHasSingleNumber() throws Exception {
    mockMvc
        .perform(get(SINGLE_ENTITY_URI.formatted(FICTION_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value("1"));
  }

  @Test
  void secondRootNodeHasCorrectNumber() throws Exception {
    mockMvc
        .perform(get(SINGLE_ENTITY_URI.formatted(NON_FICTION_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value("2"));
  }

  @Test
  void level1ChildHasTwoPartNumber() throws Exception {
    mockMvc
        .perform(get(SINGLE_ENTITY_URI.formatted(MEMOIR_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value("2.20"));
  }

  @Test
  void level2ChildHasThreePartNumber() throws Exception {
    mockMvc
        .perform(get(SINGLE_ENTITY_URI.formatted(COOKING_MEMOIR_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value("2.20.1"));
  }

  @Test
  void lastSiblingAtLevel2HasCorrectNumber() throws Exception {
    mockMvc
        .perform(get(SINGLE_ENTITY_URI.formatted(WAR_MEMOIR_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value("2.20.5"));
  }
}
