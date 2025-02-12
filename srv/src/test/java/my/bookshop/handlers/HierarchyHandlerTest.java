/* package my.bookshop.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;

import cds.gen.adminservice.GenreHierarchy;
import my.bookshop.handlers.HierarchyHandler.Sorter;

public class HierarchyHandlerTest {

	@Test
	public void testSortingRoots() {
		GenreHierarchy h1 = GenreHierarchy.create();
		h1.setName("Philosophical fiction");
		GenreHierarchy h2 = GenreHierarchy.create();
		h2.setName("Epic");
		List<GenreHierarchy> list = List.of(h1,h2);
		List<GenreHierarchy> sorted = list.stream().sorted(new Sorter()).toList();
		assertEquals("Epic", sorted.get(0).getName());
	}

	@Test
	public void testSortingChildren() {
		GenreHierarchy root = GenreHierarchy.create();
		root.setName("Folklore");
		GenreHierarchy h1 = GenreHierarchy.create();
		h1.setName("Urban legend");
		h1.setParnt(root);
		GenreHierarchy h2 = GenreHierarchy.create();
		h2.setName("Fairy tale");
		h2.setParnt(root);
		List<GenreHierarchy> list = List.of(h1,h2);
		List<GenreHierarchy> sorted = list.stream().sorted(new Sorter()).toList();
		assertEquals("Fairy tale", sorted.get(0).getName());
	}
	
	@Test
	public void testSortingChildrenWithDifRoot() {
		GenreHierarchy root1 = GenreHierarchy.create();
		root1.setName("Thriller");
		GenreHierarchy root2 = GenreHierarchy.create();
		root2.setName("Folklore");
		GenreHierarchy h2 = GenreHierarchy.create();
		h2.setName("Urban legend");
		h2.setParnt(root2);
		List<GenreHierarchy> list = List.of(root1,h2);
		List<GenreHierarchy> sorted = list.stream().sorted(new Sorter()).toList();
		assertEquals("Urban legend", sorted.get(0).getName());
	}

	@Test
	public void testSortingChildrenSameRoot() {
		GenreHierarchy root = GenreHierarchy.create();
		root.setName("Folklore");
		GenreHierarchy h1 = GenreHierarchy.create();
		h1.setName("Urban legend");
		h1.setParnt(root);
		List<GenreHierarchy> list = List.of(h1,root);
		List<GenreHierarchy> sorted = list.stream().sorted(new Sorter()).toList();
		assertEquals("Folklore", sorted.get(0).getName());
	}
}
 */