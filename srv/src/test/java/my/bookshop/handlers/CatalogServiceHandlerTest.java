package my.bookshop.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sap.cds.ql.Select;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.catalogservice.AddReviewContext;
import cds.gen.catalogservice.Books;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.Reviews;
import my.bookshop.BookRatingService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CatalogServiceHandlerTest {

	@Autowired
	private PersistenceService db;

	@Autowired
	@Qualifier(CatalogService_.CDS_NAME)
	private CdsService catalogService;

	@Autowired
	private CdsModel model;

	@Autowired
	private BookRatingService bookRatingService;

	@Test
	public void testDiscountHandler() {
		Books book1 = Books.create();
		book1.setTitle("Book 1");
		book1.setStock(10);
		Books book2 = Books.create();
		book2.setTitle("Book 2");
		book2.setStock(200);

		CatalogServiceHandler handler = new CatalogServiceHandler(db, bookRatingService, model);
		handler.discountBooks(Stream.of(book1, book2));

		assertEquals("Book 1", book1.getTitle(), "Book 1 was discounted");
		assertEquals("Book 2 -- 11% discount", book2.getTitle(), "Book 2 was not discounted");
	}

	@Test
	@WithMockUser(username = "user")
	public void testCreateReviewHandler() {
		Stream<BookReview> bookReviews = Stream.of(
				new BookReview("f846b0b9-01d4-4f6d-82a4-d79204f62278", 1, "quite bad", "disappointing..."),
				new BookReview("9b084139-0b1e-43b6-b12a-7b3669d75f02", 5, "great read", "just amazing..."));

		bookReviews.forEach(bookReview -> {
			AddReviewContext context = addReviewContext(bookReview.bookId, bookReview.rating, bookReview.title,
					bookReview.text);
			catalogService.emit(context);

			Reviews result = context.getResult();

			assertEquals(bookReview.bookId, result.getBookId());
			assertEquals(bookReview.rating, result.getRating());
			assertEquals(bookReview.title, result.getTitle());
			assertEquals(bookReview.text, result.getText());
		});
	}

	@Test
	@WithMockUser(username = "user")
	public void testAddReviewWithInvalidRating() {
		Stream<BookReview> bookReviews = Stream.of(
				// lt 1 is invalid
				new BookReview("f846b0b9-01d4-4f6d-82a4-d79204f62278", 0, "quite bad", "disappointing..."),
				// gt 5 is invalid
				new BookReview("9b084139-0b1e-43b6-b12a-7b3669d75f02", 6, "great read", "just amazing..."));

		String message = "Valid rating range needs to be within 1 and 5";

		bookReviews.forEach(bookReview -> {
			AddReviewContext context = addReviewContext(bookReview.bookId, bookReview.rating, bookReview.title,
					bookReview.text);
			assertThrows(ServiceException.class, () -> catalogService.emit(context), message);
		});
	}

	@Test
	@WithMockUser(username = "user")
	public void testAddReviewForNonExistingBook() {

		String nonExistingBookId = "non-existing";
		String exMessage1 = "You have to specify the book to review";
		String exMessage2 = String.format("A book with the specified ID '%s' does not exist", nonExistingBookId);

		Stream<BookReviewTestFixture> testCases = Stream.of(
				// no book provided
				new BookReviewTestFixture(new BookReview(null, 1, "quite bad", "disappointing..."), exMessage1),
				// invalid book id
				new BookReviewTestFixture(new BookReview(nonExistingBookId, 5, "great read", "just amazing..."),
						exMessage2));

		testCases.forEach(testCase -> {
			AddReviewContext context = addReviewContext(testCase.review.bookId, testCase.review.rating,
					testCase.review.title, testCase.review.text);
			assertThrows(ServiceException.class, () -> catalogService.emit(context), testCase.exceptionMessage);

		});
	}

	/*
	 * Holder class for book review information.
	 */
	private class BookReview {
		String bookId;
		Integer rating;
		String title;
		String text;

		BookReview(String bookId, Integer rating, String title, String text) {
			this.bookId = bookId;
			this.rating = rating;
			this.title = title;
			this.text = text;
		}
	}

	/*
	 * Holder class for a book review test case.
	 */
	private class BookReviewTestFixture {
		BookReview review;
		String exceptionMessage;

		BookReviewTestFixture(BookReview review, String exceptionMessage) {
			this.review = review;
			this.exceptionMessage = exceptionMessage;
		}
	}

	private AddReviewContext addReviewContext(String bookId, Integer rating, String title, String text) {
		AddReviewContext context = AddReviewContext.create(cds.gen.catalogservice.Books_.CDS_NAME);
		context.setCqn(Select.from(CatalogService_.BOOKS).byId(bookId));
		context.setRating(rating);
		context.setTitle(title);
		context.setText(text);
		return context;
	}

}
