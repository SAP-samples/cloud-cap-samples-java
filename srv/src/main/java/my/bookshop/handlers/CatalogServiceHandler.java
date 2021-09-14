package my.bookshop.handlers;

import static cds.gen.catalogservice.CatalogService_.BOOKS;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sap.cds.Result;
import com.sap.cds.Struct;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;
import com.sap.cds.services.persistence.PersistenceService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cds.gen.catalogservice.AddReviewContext;
import cds.gen.catalogservice.Books;
import cds.gen.catalogservice.Books_;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.Reviews;
import cds.gen.catalogservice.Reviews_;
import cds.gen.catalogservice.SubmitOrderContext;
import cds.gen.reviewservice.ReviewService_;
import my.bookshop.MessageKeys;
import my.bookshop.RatingCalculator;

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

	private final DraftService reviewService;

	private final Messages messages;

	private final RatingCalculator ratingCalculator;

	private final CqnAnalyzer analyzer;

	CatalogServiceHandler(PersistenceService db, @Qualifier(ReviewService_.CDS_NAME) DraftService reviewService,
			Messages messages, RatingCalculator ratingCalculator, CdsModel model) {
		this.db = db;
		this.reviewService = reviewService;
		this.messages = messages;
		this.ratingCalculator = ratingCalculator;
		this.analyzer = CqnAnalyzer.create(model);
	}

	/**
	 * Invokes some validations before creating a review.
	 *
	 * @param context {@link ReviewContext}
	 */
	@Before(entity = Books_.CDS_NAME)
	public void beforeAddReview(AddReviewContext context) {
		String user = context.getUserInfo().getName();
		String bookId = (String) analyzer.analyze(context.getCqn()).targetKeys().get(Books.ID);

		Result result = db.run(Select.from(CatalogService_.REVIEWS)
				.where(review -> review.book_ID().eq(bookId).and(review.createdBy().eq(user))));

		if (result.first().isPresent()) {
			throw new ServiceException(ErrorStatuses.METHOD_NOT_ALLOWED, MessageKeys.REVIEW_ADD_FORBIDDEN)
					.messageTarget(Reviews_.class, r -> r.createdBy());
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

		String bookId = (String) analyzer.analyze(context.getCqn()).targetKeys().get(Books.ID);

		cds.gen.reviewservice.Reviews review = cds.gen.reviewservice.Reviews.create();
		review.setBookId(bookId);
		review.setRating(rating);
		review.setTitle(title);
		review.setText(text);

		Result res = reviewService.run(Insert.into(ReviewService_.REVIEWS).entry(review));
		cds.gen.reviewservice.Reviews inserted = res.single(cds.gen.reviewservice.Reviews.class);

		messages.success(MessageKeys.REVIEW_ADDED);

		context.setResult(Struct.access(inserted).as(Reviews.class));
	}

	/**
	 * Recalculates and sets the book rating after a new review for the given book.
	 *
	 * @param context {@link ReviewContext}
	 */
	@After(entity = Books_.CDS_NAME)
	public void afterAddReview(AddReviewContext context) {
		ratingCalculator.setBookRating(context.getResult().getBookId());
	}

	@After(event = CqnService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null).forEach(b -> {
			loadStockIfNotSet(b);
			discountBooksWithMoreThan111Stock(b);
		});
	}

	@After
	public void setIsReviewable(CdsReadEventContext context, List<Books> books) {
		String user = context.getUserInfo().getName();
		List<String> bookIds = books.stream().filter(b -> b.getId() != null).map(b -> b.getId())
				.collect(Collectors.toList());

		if (bookIds.isEmpty()) {
			return;
		}

		CqnSelect query = Select.from(CatalogService_.BOOKS, b -> b.filter(b.ID().in(bookIds)).reviews())
				.where(r -> r.createdBy().eq(user));

		Set<String> reviewedBooks = db.run(query).streamOf(Reviews.class).map(Reviews::getBookId)
				.collect(Collectors.toSet());

		for (Books book : books) {
			if (reviewedBooks.contains(book.getId())) {
				book.setIsReviewable(false);
			}
		}
	}

	@On
	public void onSubmitOrder(SubmitOrderContext context) {
		Integer quantity = context.getQuantity();
		String bookId = context.getBook();

		Optional<Books> book = db.run(Select.from(BOOKS).columns(Books_::stock).byId(bookId)).first(Books.class);

		book.orElseThrow(() -> new ServiceException(ErrorStatuses.NOT_FOUND, MessageKeys.BOOK_MISSING)
				.messageTarget(Books_.class, b -> b.ID()));

		int stock = book.map(Books::getStock).get();

		if (stock >= quantity) {
			db.run(Update.entity(BOOKS).byId(bookId).data(Books.STOCK, stock -= quantity));

			SubmitOrderContext.ReturnType result = SubmitOrderContext.ReturnType.create();
			result.setStock(stock);

			context.setResult(result);
		} else {
			throw new ServiceException(ErrorStatuses.CONFLICT, MessageKeys.ORDER_EXCEEDS_STOCK, quantity);
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
