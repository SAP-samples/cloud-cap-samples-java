package my.bookshop.handlers;

import static cds.gen.catalogservice.CatalogService_.BOOKS;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.sap.cds.EmptyResultException;
import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.catalogservice.AddReviewContext;
import cds.gen.catalogservice.Books;
import cds.gen.catalogservice.Books_;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.ReturnCatalogServiceSubmitOrder;
import cds.gen.catalogservice.Reviews;
import cds.gen.catalogservice.Reviews_;
import cds.gen.catalogservice.SubmitOrderContext;
import my.bookshop.BookRatingService;
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

	private final PersistenceService db;

	private final Messages messages;

	private final BookRatingService bookRatingService;

	private final CqnAnalyzer analyzer;

	CatalogServiceHandler(PersistenceService db, Messages messages, BookRatingService bookRatingService,
			CdsModel model) {
		this.db = db;
		this.messages = messages;
		this.bookRatingService = bookRatingService;
		this.analyzer = CqnAnalyzer.create(model);
	}

	/**
	 * Invokes some validations before creating a review.
	 *
	 * @param context {@link ReviewContext}
	 */
	@Before(entity = Books_.CDS_NAME)
	public void beforeAddReview(AddReviewContext context) {
		String reviewer = context.getUserInfo().getName();
		String bookId = (String) analyzer.analyze(context.getCqn()).targetKeys().get(Books.ID);

		Result result = db.run(Select.from(CatalogService_.REVIEWS)
				.where(review -> review.book_ID().eq(bookId).and(review.reviewer().eq(reviewer))));

		if (result.first().isPresent()) {
			throw new ServiceException(ErrorStatuses.METHOD_NOT_ALLOWED,
					"User not allowed to add more than one review for a given book").messageTarget(Reviews_.class,
							r -> r.reviewer());
		}
	}

	/**
	 * Handles the review creation from the given context.
	 *
	 * @param context {@link ReviewContext}
	 */
	@On(entity = Books_.CDS_NAME)
	public void onAddReview(AddReviewContext context) {
		Integer rating = context.getRating();
		String title = context.getTitle();
		String text = context.getText();

		String username = context.getUserInfo().getName();
		// String bookId = (String)
		// analyzer.analyze(context.getCqn()).targetKeys().get(Books.ID);

		String bookId = validateReviewAndReturnBookId(context);

		Reviews review = Reviews.create();
		review.setBookId(bookId);
		review.setReviewer(username);
		review.setRating(rating);
		review.setTitle(title);
		review.setText(text);

		// validateReview(review);

		Result res = db.run(Insert.into(CatalogService_.REVIEWS).entry(review));
		Reviews inserted = res.single(Reviews.class);

		messages.success(MessageKeys.REVIEW_ADDED);
		context.setResult(inserted);
	}

	/**
	 * Recalculates and sets the book rating after a new review for the given book.
	 *
	 * @param context {@link ReviewContext}
	 */
	@After(entity = Books_.CDS_NAME)
	public void afterAddReview(AddReviewContext context) {
		bookRatingService.setBookRating(context.getResult().getBookId());
	}

	@After(event = CdsService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null).forEach(b -> {
			loadStockIfNotSet(b);
			discountBooksWithMoreThan111Stock(b);
		});
	}

	@After
	public void setIsReviewable(CdsReadEventContext context, List<Books> books) {
		String user = context.getUserInfo().getName();

		Result result = db
				.run(Select.from(CatalogService_.REVIEWS).columns(Reviews_::book_ID).where(r -> r.reviewer().eq(user)));
		Set<String> reviewedBooks = result.streamOf(Reviews.class).map(Reviews::getBookId).collect(Collectors.toSet());

		for (Books book : books) {
			if (reviewedBooks.contains(book.getId())) {
				book.setIsReviewable(false);
			}
		}
	}

	@On
	public void onSubmitOrder(SubmitOrderContext context) {
		Integer amount = context.getAmount();
		String bookId = context.getBook();

		Optional<Books> book = db.run(Select.from(BOOKS).columns(Books_::stock).byId(bookId)).first(Books.class);

		book.orElseThrow(() -> new ServiceException(ErrorStatuses.NOT_FOUND, MessageKeys.BOOK_MISSING)
				.messageTarget(Books_.class, b -> b.ID()));

		Integer stock = book.map(Books::getStock).get();

		if (stock >= amount) {
			db.run(Update.entity(BOOKS).byId(bookId).data(Books.STOCK, stock -= amount));

			ReturnCatalogServiceSubmitOrder result = ReturnCatalogServiceSubmitOrder.create();
			result.setStock(stock);

			context.setResult(result);
			context.setCompleted();
		} else {
			throw new ServiceException(ErrorStatuses.CONFLICT, String.format("%d exceeds stock for book", amount));
		}
	}

	private void discountBooksWithMoreThan111Stock(Books b) {
		if (b.getStock() != null && b.getStock() > 111) {
			b.setTitle(String.format("%s -- 11%% discount", b.getTitle()));
		}
	}

	private void loadStockIfNotSet(Books b) {
		if (b.getId() != null && b.getStock() == null) {
			b.setStock(
					db.run(Select.from(BOOKS).byId(b.getId()).columns(Books_::stock)).single(Books.class).getStock());
		}
	}

	private String validateReviewAndReturnBookId(AddReviewContext context) {
		validateRating(context);
		return validateBook(context);
	}

	private void validateRating(AddReviewContext context) {
		Integer rating = context.getRating();
		if (rating == null || rating < 1 || rating > 5) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.REVIEW_INVALID_RATING)
			.messageTarget(Reviews_.class, r -> r.rating());
		}
	}

	private String validateBook(AddReviewContext context) {
		try {
			Books book = db.run(context.getCqn()).single(Books.class);
			return book.getId();
		} catch (EmptyResultException ex) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.BOOK_MISSING);
		}
	}

}
