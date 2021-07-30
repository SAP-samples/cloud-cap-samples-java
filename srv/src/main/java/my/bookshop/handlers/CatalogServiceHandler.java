package my.bookshop.handlers;

import static cds.gen.catalogservice.CatalogService_.BOOKS;

import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.Row;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.catalogservice.Books;
import cds.gen.catalogservice.Books_;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.SubmitOrderContext;
import cds.gen.reviewservice.ReviewService_;
import cds.gen.reviewservice.Reviewed;
import cds.gen.reviewservice.ReviewedContext;
import my.bookshop.MessageKeys;

/**
 * Custom business logic for the "Catalog Service" (see cat-service.cds)
 *
 * Handles Reading of Books
 *
 * Adds Discount Message to the Book Title if too much stock is available
 *
 * Provides adding book reviews
 */
@Component
@ServiceName(CatalogService_.CDS_NAME)
class CatalogServiceHandler implements EventHandler {

	private static final Logger logger = LoggerFactory.getLogger(CatalogServiceHandler.class);
	private final PersistenceService db;

	@Autowired
	@Qualifier(CatalogService_.CDS_NAME)
	private CqnService catalogService;

	CatalogServiceHandler(PersistenceService db, CdsModel model) {
		this.db = db;
	}

	@On(service = ReviewService_.CDS_NAME)
	private void reviewAdded(ReviewedContext context) {
		Reviewed event = context.getData();
		Row row = catalogService.run(Select.from(CatalogService_.BOOKS).byId(event.getSubject())).first().orElse(null);
		// update the book rating
		if (row != null) {
			Books book = row.as(Books.class);

			Result res = db.run(Update.entity(cds.gen.my.bookshop.Books_.CDS_NAME).byId(book.getId()).data(Books.RATING, event.getRating()));
			if (res.rowCount() > 0) {
				logger.info("Rating of '{}' has been updated to '{}'.", book.getTitle(), event.getRating());
			} else {

			}
		}
	}

	@After(event = CqnService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null).forEach(b -> {
			loadStockIfNotSet(b);
			discountBooksWithMoreThan111Stock(b);
		});
	}

	@On
	public void onSubmitOrder(SubmitOrderContext context) {
		Integer amount = context.getAmount();
		String bookId = context.getBook();

		Optional<Books> book = db.run(Select.from(BOOKS).columns(Books_::stock).byId(bookId)).first(Books.class);

		book.orElseThrow(() -> new ServiceException(ErrorStatuses.NOT_FOUND, MessageKeys.BOOK_MISSING)
				.messageTarget(Books_.class, b -> b.ID()));

		int stock = book.map(Books::getStock).get();

		if (stock >= amount) {
			db.run(Update.entity(BOOKS).byId(bookId).data(Books.STOCK, stock -= amount));

			SubmitOrderContext.ReturnType result = SubmitOrderContext.ReturnType.create();
			result.setStock(stock);

			context.setResult(result);
		} else {
			throw new ServiceException(ErrorStatuses.CONFLICT, MessageKeys.ORDER_EXCEEDS_STOCK, amount);
		}
	}

	private void discountBooksWithMoreThan111Stock(Books b) {
		if (b.getStock() != null && b.getStock() > 111) {
			b.setTitle(String.format("%s -- 11%% discount", b.getTitle()));
		}
	}

	private void loadStockIfNotSet(Books b) {
		if (b.getId() != null && b.getStock() == null) {
			b.setStock(db.run(Select.from(BOOKS).byId(b.getId()).columns(Books_::stock)).single(Books.class).getStock());
		}
	}

}
