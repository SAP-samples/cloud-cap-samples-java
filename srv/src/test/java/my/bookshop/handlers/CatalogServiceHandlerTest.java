package my.bookshop.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.sap.cds.services.request.FeatureTogglesInfo;

import cds.gen.catalogservice.Books;

class CatalogServiceHandlerTest {

	@Test
	void discountHandler() {
		Books book1 = Books.create();
		book1.setTitle("Book 1");
		book1.setStock(10);
		Books book2 = Books.create();
		book2.setTitle("Book 2");
		book2.setStock(200);

		CatalogServiceHandler handler = new CatalogServiceHandler(null, null, null, FeatureTogglesInfo.create(), null, null);
		handler.discountBooks(Stream.of(book1, book2));

		assertEquals("Book 1", book1.getTitle(), "Book 1 was discounted");
		assertEquals("Book 2 -- 11% discount", book2.getTitle(), "Book 2 was not discounted");
	}
}
