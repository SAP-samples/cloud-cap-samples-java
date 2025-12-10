package my.bookshop.handlers;

import static cds.gen.catalogservice.CatalogService_.BOOKS;

import cds.gen.catalogservice.Books;
import cds.gen.catalogservice.BooksAddReviewContext;
import cds.gen.catalogservice.Books_;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.Reviews;
import cds.gen.catalogservice.SubmitOrderContext;
import cds.gen.reviewservice.ReviewService;
import cds.gen.reviewservice.ReviewService_;
import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.FeatureTogglesInfo;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import my.bookshop.MessageKeys;
import my.bookshop.RatingCalculator;
import org.springframework.stereotype.Component;

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
	private final ReviewService reviewService;

	private final Messages messages;
	private final FeatureTogglesInfo featureToggles;
	private final RatingCalculator ratingCalculator;
	private final CqnAnalyzer analyzer;

	CatalogServiceHandler(PersistenceService db, ReviewService reviewService, Messages messages,
			FeatureTogglesInfo featureToggles, RatingCalculator ratingCalculator, CdsModel model) {
		this.db = db;
		this.reviewService = reviewService;
		this.messages = messages;
		this.featureToggles = featureToggles;
		this.ratingCalculator = ratingCalculator;
		this.analyzer = CqnAnalyzer.create(model);
	}

	@Before(entity = Books_.CDS_NAME)
	public void alwaysSelectStock(CdsReadEventContext context) {
		CqnSelect copy = CQL.copy(context.getCqn(), new Modifier() {
			@Override
			public List<CqnSelectListItem> items(List<CqnSelectListItem> items) {
				var paths = items.stream().filter(i -> i.isRef()).map(i -> i.asRef().path()).collect(Collectors.toSet());
				if (paths.contains(Books.TITLE) && !paths.contains(Books.STOCK)) {
					items.add(CQL.get(Books.STOCK));
				}
				return items;
			}
		});
		context.setCqn(copy);
	}

	/*
	 * Invokes some validations before creating a review.
	 */
	@Before
	public void beforeAddReview(Books_ ref, BooksAddReviewContext context) {
		String user = context.getUserInfo().getName();

		var result = db.run(Select.from(ref.reviews())
				.where(review -> review.createdBy().eq(user)));

		if (result.first().isPresent()) {
			throw new ServiceException(ErrorStatuses.METHOD_NOT_ALLOWED, MessageKeys.REVIEW_ADD_FORBIDDEN);
		}
	}

	/**
	 * Handles the review creation from the given context.
	 *
	 * @param context {@link ReviewContext}
	 */
	@On
	public Reviews onAddReview(Books_ ref, BooksAddReviewContext context) {
		String bookId = (String) analyzer.analyze(context.getCqn()).targetKeys().get(Books.ID);
		cds.gen.reviewservice.Reviews review = cds.gen.reviewservice.Reviews.create();
		review.setBookId(bookId);
		review.setRating(context.getRating());
		review.setTitle(context.getTitle());
		review.setText(context.getText());

		Result res = reviewService.run(Insert.into(ReviewService_.REVIEWS).entry(review));

		messages.success(MessageKeys.REVIEW_ADDED);
		return res.single(Reviews.class);
	}

	/**
	 * Recalculates and sets the book rating after a new review for the given book.
	 *
	 * @param context {@link ReviewContext}
	 */
	@After(entity = Books_.CDS_NAME)
	public void afterAddReview(BooksAddReviewContext context) {
		ratingCalculator.setBookRating(context.getResult().getBookId());
	}

	@After(event = CqnService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null).forEach(b -> {
			discountBooksWithMoreThan111Stock(b, featureToggles.isEnabled("discount"));
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

		var query = Select.from(BOOKS, b -> b.filter(b.ID().in(bookIds)).reviews())
				.where(r -> r.createdBy().eq(user));

		Set<String> reviewedBooks = db.run(query).stream().map(Reviews::getBookId)
				.collect(Collectors.toSet());

		for (Books book : books) {
			if (reviewedBooks.contains(book.getId())) {
				book.setIsReviewable(false);
			}
		}
	}

	@On
	public SubmitOrderContext.ReturnType onSubmitOrder(SubmitOrderContext context) {
		Integer quantity = context.getQuantity();
		String bookId = context.getBook();

		Books book = db.run(Select.from(BOOKS).columns(Books_::stock).byId(bookId)).single();
		int stock = book.getStock();

		if (stock >= quantity) {
			db.run(Update.entity(BOOKS).byId(bookId).data(Books.STOCK, stock -= quantity));

			SubmitOrderContext.ReturnType result = SubmitOrderContext.ReturnType.create();
			result.setStock(stock);
			return result;
		} else {
			throw new ServiceException(ErrorStatuses.CONFLICT, MessageKeys.ORDER_EXCEEDS_STOCK, quantity);
		}
	}

	private void discountBooksWithMoreThan111Stock(Books b, boolean premium) {
		if (b.getStock() != null && b.getStock() > 111) {
			b.setTitle("%s -- %s%% discount".formatted(b.getTitle(), premium ? 14 : 11));
		}
	}

}
