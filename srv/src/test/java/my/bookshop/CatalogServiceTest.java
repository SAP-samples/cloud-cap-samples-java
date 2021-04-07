package my.bookshop;

import static cds.gen.catalogservice.CatalogService_.BOOKS;
import static cds.gen.catalogservice.CatalogService_.REVIEWS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Select;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.catalogservice.AddReviewContext;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.Reviews;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CatalogServiceTest {

	@Autowired
	@Qualifier(CatalogService_.CDS_NAME)
	private CqnService catalogService;

	@Autowired
	private PersistenceService db;

	@AfterEach
	public void cleanup() {
		db.run(Delete.from(REVIEWS));
	}

	@Test
	@WithMockUser(username = "user")
	public void testCreateReviewHandler() {
		Stream<Reviews> bookReviews = Stream.of(
				createReview("f846b0b9-01d4-4f6d-82a4-d79204f62278", 1, "quite bad", "disappointing..."),
				createReview("aebdfc8a-0dfa-4468-bd36-48aabd65e663", 5, "great read", "just amazing..."));

		bookReviews.forEach(bookReview -> {
			AddReviewContext context = addReviewContext(bookReview);
			catalogService.emit(context);

			Reviews result = context.getResult();

			assertEquals(bookReview.getBookId(), result.getBookId());
			assertEquals(bookReview.getRating(), result.getRating());
			assertEquals(bookReview.getTitle(), result.getTitle());
			assertEquals(bookReview.getText(), result.getText());
		});
	}

	@Test
	@WithMockUser(username = "user")
	public void testAddReviewWithInvalidRating() {
		Stream<Reviews> bookReviews = Stream.of(
				// lt 1 is invalid
				createReview("f846b0b9-01d4-4f6d-82a4-d79204f62278", 0, "quite bad", "disappointing..."),
				// gt 5 is invalid
				createReview("9b084139-0b1e-43b6-b12a-7b3669d75f02", 6, "great read", "just amazing..."));

		String message = "Valid rating range needs to be within 1 and 5";

		bookReviews.forEach(bookReview -> {
			AddReviewContext context = addReviewContext(bookReview);
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
				new BookReviewTestFixture(createReview(null, 1, "quite bad", "disappointing..."), exMessage1),
				// invalid book id
				new BookReviewTestFixture(createReview(nonExistingBookId, 5, "great read", "just amazing..."),
						exMessage2));

		testCases.forEach(testCase -> {
			AddReviewContext context = addReviewContext(testCase.review);
			assertThrows(ServiceException.class, () -> catalogService.emit(context), testCase.exceptionMessage);
		});
	}

	@Test
	@WithMockUser(username = "user")
	public void testAddReviewSameBookMoreThanOnceBySameUser() {

		String bookId = "4a519e61-3c3a-4bd9-ab12-d7e0c5329933";
		String anotherBookId = "9b084139-0b1e-43b6-b12a-7b3669d75f02";

		AddReviewContext firstReview = addReviewContext(createReview(bookId, 1, "quite bad", "disappointing..."));
		AddReviewContext secondReview = addReviewContext(createReview(bookId, 5, "great read", "just amazing..."));
		AddReviewContext anotherReview = addReviewContext(createReview(anotherBookId, 4, "very good", "entertaining..."));

		assertDoesNotThrow(() -> catalogService.emit(firstReview));
		assertThrows(ServiceException.class, () -> catalogService.emit(secondReview),
				"User not allowed to add more than one review for a given book");
		assertDoesNotThrow(() -> catalogService.emit(anotherReview));
	}

	private Reviews createReview(String bookId, Integer rating, String title, String text) {
		Reviews review = Reviews.create();
		review.setBookId(bookId);
		review.setRating(rating);
		review.setTitle(title);
		review.setText(text);
		return review;
	}

	/*
	 * Holder class for a book review test case.
	 */
	private class BookReviewTestFixture {
		Reviews review;
		String exceptionMessage;

		BookReviewTestFixture(Reviews review, String exceptionMessage) {
			this.review = review;
			this.exceptionMessage = exceptionMessage;
		}
	}

	private AddReviewContext addReviewContext(Reviews review) {
		AddReviewContext context = AddReviewContext.create();
		context.setCqn(Select.from(BOOKS).byId(review.getBookId()));
		context.setRating(review.getRating());
		context.setTitle(review.getTitle());
		context.setText(review.getText());
		return context;
	}

}
