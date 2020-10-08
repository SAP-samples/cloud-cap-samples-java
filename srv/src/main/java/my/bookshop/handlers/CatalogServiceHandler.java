package my.bookshop.handlers;

import static cds.gen.catalogservice.CatalogService_.BOOKS;

import java.util.stream.Stream;

import cds.gen.catalogservice.Books_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.catalogservice.Books;
import cds.gen.catalogservice.CatalogService_;

/**
 * Custom business logic for the "Catalog Service" (see cat-service.cds)
 *
 * Handles Reading of Books
 * Adds Discount Message to the Book Title if too much stock is available
 */
@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogServiceHandler implements EventHandler {

	private final PersistenceService db;

	public CatalogServiceHandler(PersistenceService db) {
		this.db = db;
	}

	@After(event = CdsService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null).forEach(
			b -> {
				loadStockIfNotSet(b);
				discountBooksWithMoreThan111Stock(b);
			}
		);
	}

	private void discountBooksWithMoreThan111Stock(Books b) {
		if(b.getStock() > 111) {
			b.setTitle(String.format("%s -- 11%% discount", b.getTitle()));
		}
	}

	private void loadStockIfNotSet(Books b) {
		if (b.getStock() == null) {
			b.setStock(db.run(Select.from(BOOKS).byId(b.getId()).columns(Books_::stock)).single(Books.class).getStock());
		}
	}
}
