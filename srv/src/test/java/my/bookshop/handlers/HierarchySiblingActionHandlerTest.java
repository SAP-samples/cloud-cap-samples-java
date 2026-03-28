package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.GENRE_HIERARCHY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cds.gen.adminservice.GenreHierarchy;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for {@link HierarchySiblingActionHandler} verifying the moveSibling action that
 * reorders sibling nodes in the GenreHierarchy.
 *
 * <p>Uses the children of "Memoir" (parent ID ...059) which has 5 children ordered by siblingRank:
 *
 * <ol>
 *   <li>Cooking Memoir (...150)
 *   <li>Graphic Memoir (...178)
 *   <li>Parenting Memoir (...217)
 *   <li>Travel Memoir (...263)
 *   <li>War Memoir (...270)
 * </ol>
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class HierarchySiblingActionHandlerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private PersistenceService db;

  private static final String MEMOIR_PARENT_ID = "8bbf14c6-b378-4e35-9b4f-05a9c8878059";

  private static final String COOKING_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878150";
  private static final String GRAPHIC_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878178";
  private static final String PARENTING_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878217";
  private static final String TRAVEL_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878263";
  private static final String WAR_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878270";

  private static final String MOVE_SIBLING_URI =
      "/api/admin/GenreHierarchy(ID=%s,IsActiveEntity=true)/AdminService.moveSibling";

  /** Stores original sibling ranks to restore after each test. */
  private List<GenreHierarchy> originalSiblings;

  @BeforeEach
  void saveOriginalOrder() {
    originalSiblings =
        db.run(
                Select.from(GENRE_HIERARCHY)
                    .columns(c -> c.ID(), c -> c.siblingRank())
                    .where(c -> c.parent_ID().eq(MEMOIR_PARENT_ID))
                    .orderBy(c -> c.siblingRank().asc()))
            .list();
  }

  @AfterEach
  void restoreOriginalOrder() {
    db.run(com.sap.cds.ql.Update.entity(GENRE_HIERARCHY).entries(originalSiblings));
  }

  @Test
  void moveNodeForward() throws Exception {
    // Move Cooking Memoir (1st) before Travel Memoir (4th)
    // Before: Cooking, Graphic, Parenting, Travel, War
    // After:  Graphic, Parenting, Cooking, Travel, War
    moveSibling(COOKING_MEMOIR, TRAVEL_MEMOIR);

    assertSiblingOrder(GRAPHIC_MEMOIR, PARENTING_MEMOIR, COOKING_MEMOIR, TRAVEL_MEMOIR, WAR_MEMOIR);
  }

  @Test
  void makeNodeLastSibling() throws Exception {
    // Move Graphic Memoir (2nd) to the end (NextSibling = null)
    // Before: Cooking, Graphic, Parenting, Travel, War
    // After:  Cooking, Parenting, Travel, War, Graphic
    moveSiblingToEnd(GRAPHIC_MEMOIR);

    assertSiblingOrder(COOKING_MEMOIR, PARENTING_MEMOIR, TRAVEL_MEMOIR, WAR_MEMOIR, GRAPHIC_MEMOIR);
  }

  @Test
  void moveNodeBackward() throws Exception {
    // Move Travel Memoir (4th) before Graphic Memoir (2nd)
    // Before: Cooking, Graphic, Parenting, Travel, War
    // After:  Cooking, Travel, Graphic, Parenting, War
    moveSibling(TRAVEL_MEMOIR, GRAPHIC_MEMOIR);

    assertSiblingOrder(COOKING_MEMOIR, TRAVEL_MEMOIR, GRAPHIC_MEMOIR, PARENTING_MEMOIR, WAR_MEMOIR);
  }

  @Test
  void makeNodeFirstSibling() throws Exception {
    // Move War Memoir (last) before Cooking Memoir (1st)
    // Before: Cooking, Graphic, Parenting, Travel, War
    // After:  War, Cooking, Graphic, Parenting, Travel
    moveSibling(WAR_MEMOIR, COOKING_MEMOIR);

    assertSiblingOrder(WAR_MEMOIR, COOKING_MEMOIR, GRAPHIC_MEMOIR, PARENTING_MEMOIR, TRAVEL_MEMOIR);
  }

  private void moveSibling(String nodeId, String nextSiblingId) throws Exception {
    String body =
        """
        {"NextSibling": {"ID": "%s"}}"""
            .formatted(nextSiblingId);
    mockMvc
        .perform(
            post(MOVE_SIBLING_URI.formatted(nodeId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNoContent());
  }

  private void moveSiblingToEnd(String nodeId) throws Exception {
    String body =
        """
        {"NextSibling": null}""";
    mockMvc
        .perform(
            post(MOVE_SIBLING_URI.formatted(nodeId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNoContent());
  }

  private void assertSiblingOrder(String... expectedIds) {
    List<GenreHierarchy> siblings =
        db.run(
                Select.from(GENRE_HIERARCHY)
                    .columns(c -> c.ID(), c -> c.siblingRank())
                    .where(c -> c.parent_ID().eq(MEMOIR_PARENT_ID))
                    .orderBy(c -> c.siblingRank().asc()))
            .list();

    assertThat(siblings).hasSize(expectedIds.length);
    for (int i = 0; i < expectedIds.length; i++) {
      assertThat(siblings.get(i).getId()).as("sibling at position %d", i).isEqualTo(expectedIds[i]);
      assertThat(siblings.get(i).getSiblingRank()).as("siblingRank at position %d", i).isEqualTo(i);
    }
  }
}
