package my.bookshop.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;

import cds.gen.adminservice.GenreHierarchy;
import my.bookshop.handlers.HierarchyHandler.Sorter;

public class HierarchyHandlerSorterTest {

	@Test
	public void testSortRoots() {
		GenreHierarchy r1 = genre("Philosophical fiction");
		GenreHierarchy r2 = genre("Epic");
		List<GenreHierarchy> sorted = sorted(r1, r2);

		assertEquals("Epic", sorted.get(0).getName());
		assertEquals("Philosophical fiction", sorted.get(1).getName());
	}

	@Test
	public void testSortSiblings() {
		GenreHierarchy root = genre("Folklore");
		GenreHierarchy g1 = genre("Urban legend", root);
		GenreHierarchy g2 = genre("Fairy tale", root);
		List<GenreHierarchy> sorted = sorted(g1, g2);

		assertEquals("Fairy tale", sorted.get(0).getName());
		assertEquals("Urban legend", sorted.get(1).getName());
	}

	@Test
	public void testSortChildrenWithDifRoot() {
		GenreHierarchy r1 = genre("Thriller");
		GenreHierarchy r2 = genre("Folklore");
		GenreHierarchy g2 = genre("Urban legend", r2);
		List<GenreHierarchy> sorted = sorted(r1, g2);

		assertEquals("Urban legend", sorted.get(0).getName());
		assertEquals("Thriller", sorted.get(1).getName());
	}

	@Test
	public void testSortChildrenSameRoot() {
		GenreHierarchy r1 = genre("Folklore");
		GenreHierarchy g1 = genre("Urban legend", r1);
		List<GenreHierarchy> sorted = sorted(g1, r1);

		assertEquals("Folklore", sorted.get(0).getName());
		assertEquals("Urban legend", sorted.get(1).getName());
	}

	private static GenreHierarchy genre(String name, GenreHierarchy parent) {
		GenreHierarchy genre = GenreHierarchy.create();
		genre.setName(name);
		if (parent != null) {
			genre.setParnt(parent);
		}
		return genre;
	}

	private static GenreHierarchy genre(String name) {
		return genre(name, null);
	}

	private static List<GenreHierarchy> sorted(GenreHierarchy... h) {
		return List.of(h).stream().sorted(new Sorter()).toList();
	}
}
