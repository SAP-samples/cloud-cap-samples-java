package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.GENRE_HIERARCHY;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.GenreHierarchy;
import cds.gen.adminservice.GenreHierarchy_;
import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Computes the virtual {@code number} element of {@link GenreHierarchy} on read.
 *
 * <p>The number is the dot-separated path of {@code siblingRank} values from the root to the node,
 * e.g. {@code "2.20.1"} for a node at depth 3.
 *
 * <p>Only the ancestors that are actually needed are loaded from the database, one query per tree
 * level. This scales well for large data sets with bounded tree depth.
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
public class HierarchyNumberHandler implements EventHandler {

  private final PersistenceService db;

  HierarchyNumberHandler(PersistenceService db) {
    this.db = db;
  }

  @After(event = CqnService.EVENT_READ, entity = GenreHierarchy_.CDS_NAME)
  void computeNumber(List<GenreHierarchy> genres) {
    if (genres.isEmpty()) {
      return;
    }

    // Seed with IDs of result nodes
    Set<String> needed =
        genres.stream()
            .map(GenreHierarchy::getId)
            .filter(id -> id != null)
            .collect(Collectors.toCollection(HashSet::new));

    if (needed.isEmpty()) {
      return;
    }

    // Load result nodes and iteratively their ancestors, one round per tree level
    Map<String, GenreHierarchy> known = new HashMap<>();
    while (!needed.isEmpty()) {
      List<GenreHierarchy> loaded =
          db.run(
                  Select.from(GENRE_HIERARCHY)
                      .columns(c -> c.ID(), c -> c.siblingRank(), c -> c.parent_ID())
                      .where(c -> c.ID().in(needed)))
              .list();
      needed.clear();
      for (GenreHierarchy g : loaded) {
        known.put(g.getId(), g);
        if (g.getParentId() != null && !known.containsKey(g.getParentId())) {
          needed.add(g.getParentId());
        }
      }
    }

    // Compute the number for each result node
    for (GenreHierarchy g : genres) {
      if (g.getId() == null) {
        continue;
      }
      GenreHierarchy current = known.get(g.getId());
      if (current == null || current.getSiblingRank() == null) {
        continue;
      }
      String number = String.valueOf(current.getSiblingRank());
      current = current.getParentId() != null ? known.get(current.getParentId()) : null;
      while (current != null && current.getSiblingRank() != null) {
        number = current.getSiblingRank() + "." + number;
        current = current.getParentId() != null ? known.get(current.getParentId()) : null;
      }
      g.setNumber(number);
    }
  }
}
