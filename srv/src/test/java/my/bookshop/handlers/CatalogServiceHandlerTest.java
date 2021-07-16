package my.bookshop.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.catalogservice.Books;

@ExtendWith(MockitoExtension.class)
public class CatalogServiceHandlerTest {

	// mocks are not called since stock is set on the books
	@Mock
	private PersistenceService db;

	@Mock
	private CdsModel model;

	@Test
	public void testDiscountHandler() {
		Books book1 = Books.create();
		book1.setTitle("Book 1");
		book1.setStock(10);
		Books book2 = Books.create();
		book2.setTitle("Book 2");
		book2.setStock(200);

		CatalogServiceHandler handler = new CatalogServiceHandler(db, model);
		handler.discountBooks(Stream.of(book1, book2));

		assertEquals("Book 1", book1.getTitle(), "Book 1 was discounted");
		assertEquals("Book 2 -- 11% discount", book2.getTitle(), "Book 2 was not discounted");
	}
}
