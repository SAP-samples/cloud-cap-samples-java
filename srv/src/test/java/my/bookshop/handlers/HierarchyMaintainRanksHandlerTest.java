package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.GENRE_HIERARCHY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cds.gen.adminservice.GenreHierarchy;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.Upsert;
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
 * Integration tests for {@link HierarchyMaintainRanksHandler} verifying that siblingRank values are
 * maintained when nodes are reparented or deleted.
 *
 * <p>Uses children of "Memoir" (...059) with 5 children (ranks 1-5):
 *
 * <ol>
 *   <li>Cooking Memoir (...150, rank 1)
 *   <li>Graphic Memoir (...178, rank 2)
 *   <li>Parenting Memoir (...217, rank 3)
 *   <li>Travel Memoir (...263, rank 4)
 *   <li>War Memoir (...270, rank 5)
 * </ol>
 *
 * "Fiction" (...001) is used as the target parent for reparent tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin")
class HierarchyMaintainRanksHandlerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private PersistenceService db;

  private static final String MEMOIR_ID = "8bbf14c6-b378-4e35-9b4f-05a9c8878059";
  private static final String FICTION_ID = "8bbf14c6-b378-4e35-9b4f-05a9c8878001";

  private static final String COOKING_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878150";
  private static final String GRAPHIC_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878178";
  private static final String PARENTING_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878217";
  private static final String TRAVEL_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878263";
  private static final String WAR_MEMOIR = "8bbf14c6-b378-4e35-9b4f-05a9c8878270";

  private static final String ENTITY_URI = "/api/admin/GenreHierarchy(ID=%s,IsActiveEntity=true)";

  private List<GenreHierarchy> originalMemoirChildren;
  private int originalFictionChildCount;

  @BeforeEach
  void saveOriginalState() {
    originalMemoirChildren =
        db.run(
                Select.from(GENRE_HIERARCHY)
                    .columns(c -> c.ID(), c -> c.siblingRank(), c -> c.parent_ID())
                    .where(c -> c.parent_ID().eq(MEMOIR_ID))
                    .orderBy(c -> c.siblingRank().asc()))
            .list();
    originalFictionChildCount =
        (int)
            db.run(
                    Select.from(GENRE_HIERARCHY)
                        .columns(c -> c.ID())
                        .where(c -> c.parent_ID().eq(FICTION_ID)))
                .rowCount();
  }

  @AfterEach
  void restoreOriginalState() {
    // Restore memoir children: move back any reparented nodes and reset ranks
    for (GenreHierarchy g : originalMemoirChildren) {
      db.run(Upsert.into(GENRE_HIERARCHY).entry(g));
    }
    // Reset fiction children ranks that were shifted
    List<GenreHierarchy> fictionChildren =
        db.run(
                Select.from(GENRE_HIERARCHY)
                    .columns(c -> c.ID(), c -> c.siblingRank())
                    .where(c -> c.parent_ID().eq(FICTION_ID))
                    .orderBy(c -> c.siblingRank().asc()))
            .list();
    for (int i = 0; i < fictionChildren.size(); i++) {
      fictionChildren.get(i).setSiblingRank(i + 1);
    }
    db.run(Update.entity(GENRE_HIERARCHY).entries(fictionChildren));
  }

  @Test
  void reparentNodeAdjustsOldParentRanks() throws Exception {
    // Move Cooking Memoir (rank 1) from Memoir to Fiction
    mockMvc
        .perform(
            patch(ENTITY_URI.formatted(COOKING_MEMOIR))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"parent_ID\": \"%s\"}".formatted(FICTION_ID)))
        .andExpect(status().isOk());

    // Old parent (Memoir): gap at rank 1 should be closed -> ranks 1,2,3,4
    assertSiblingOrder(MEMOIR_ID, GRAPHIC_MEMOIR, PARENTING_MEMOIR, TRAVEL_MEMOIR, WAR_MEMOIR);
  }

  @Test
  void reparentNodeGetsRank1UnderNewParent() throws Exception {
    // Move Cooking Memoir from Memoir to Fiction
    mockMvc
        .perform(
            patch(ENTITY_URI.formatted(COOKING_MEMOIR))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"parent_ID\": \"%s\"}".formatted(FICTION_ID)))
        .andExpect(status().isOk());

    // New parent (Fiction): Cooking Memoir should be rank 1
    GenreHierarchy moved =
        db.run(
                Select.from(GENRE_HIERARCHY)
                    .columns(c -> c.siblingRank(), c -> c.parent_ID())
                    .where(c -> c.ID().eq(COOKING_MEMOIR)))
            .single();
    assertThat(moved.getParentId()).isEqualTo(FICTION_ID);
    assertThat(moved.getSiblingRank()).isEqualTo(1);

    // Previous rank-1 child of Fiction should now be rank 2
    List<GenreHierarchy> fictionChildren =
        db.run(
                Select.from(GENRE_HIERARCHY)
                    .columns(c -> c.ID(), c -> c.siblingRank())
                    .where(c -> c.parent_ID().eq(FICTION_ID))
                    .orderBy(c -> c.siblingRank().asc()))
            .list();
    assertThat(fictionChildren).hasSize(originalFictionChildCount + 1);
    assertThat(fictionChildren.get(0).getId()).isEqualTo(COOKING_MEMOIR);
    assertThat(fictionChildren.get(0).getSiblingRank()).isEqualTo(1);
    assertThat(fictionChildren.get(1).getSiblingRank()).isEqualTo(2);
  }

  @Test
  void reparentMiddleNodeClosesGap() throws Exception {
    // Move Parenting Memoir (rank 3) from Memoir to Fiction
    mockMvc
        .perform(
            patch(ENTITY_URI.formatted(PARENTING_MEMOIR))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"parent_ID\": \"%s\"}".formatted(FICTION_ID)))
        .andExpect(status().isOk());

    // Old parent: ranks should be 1,2,3,4 (gap at 3 closed)
    assertSiblingOrder(MEMOIR_ID, COOKING_MEMOIR, GRAPHIC_MEMOIR, TRAVEL_MEMOIR, WAR_MEMOIR);
  }

  @Test
  void reparentViaNestedAssociation() throws Exception {
    // Move Travel Memoir (rank 4) from Memoir to Fiction using nested association syntax
    mockMvc
        .perform(
            patch(ENTITY_URI.formatted(TRAVEL_MEMOIR))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"parent\": {\"ID\": \"%s\"}}".formatted(FICTION_ID)))
        .andExpect(status().isOk());

    // Old parent: gap at rank 4 closed -> ranks 1,2,3,4
    assertSiblingOrder(MEMOIR_ID, COOKING_MEMOIR, GRAPHIC_MEMOIR, PARENTING_MEMOIR, WAR_MEMOIR);

    // Moved node should be rank 1 under Fiction
    GenreHierarchy moved =
        db.run(
                Select.from(GENRE_HIERARCHY)
                    .columns(c -> c.siblingRank(), c -> c.parent_ID())
                    .where(c -> c.ID().eq(TRAVEL_MEMOIR)))
            .single();
    assertThat(moved.getParentId()).isEqualTo(FICTION_ID);
    assertThat(moved.getSiblingRank()).isEqualTo(1);
  }

  @Test
  void deleteNodeClosesGap() throws Exception {
    // Delete Graphic Memoir (rank 2)
    mockMvc.perform(delete(ENTITY_URI.formatted(GRAPHIC_MEMOIR))).andExpect(status().isNoContent());

    // Remaining siblings should have contiguous ranks 1,2,3,4
    assertSiblingOrder(MEMOIR_ID, COOKING_MEMOIR, PARENTING_MEMOIR, TRAVEL_MEMOIR, WAR_MEMOIR);
  }

  @Test
  void deleteLastNodeClosesGap() throws Exception {
    // Delete War Memoir (rank 5, last)
    mockMvc.perform(delete(ENTITY_URI.formatted(WAR_MEMOIR))).andExpect(status().isNoContent());

    // Remaining siblings: ranks 1,2,3,4 unchanged
    assertSiblingOrder(MEMOIR_ID, COOKING_MEMOIR, GRAPHIC_MEMOIR, PARENTING_MEMOIR, TRAVEL_MEMOIR);
  }

  private void assertSiblingOrder(String parentId, String... expectedIds) {
    List<GenreHierarchy> siblings =
        db.run(
                Select.from(GENRE_HIERARCHY)
                    .columns(c -> c.ID(), c -> c.siblingRank())
                    .where(c -> c.parent_ID().eq(parentId))
                    .orderBy(c -> c.siblingRank().asc()))
            .list();

    assertThat(siblings).hasSize(expectedIds.length);
    for (int i = 0; i < expectedIds.length; i++) {
      assertThat(siblings.get(i).getId()).as("sibling at position %d", i).isEqualTo(expectedIds[i]);
      assertThat(siblings.get(i).getSiblingRank())
          .as("siblingRank at position %d", i)
          .isEqualTo(i + 1);
    }
  }
}
