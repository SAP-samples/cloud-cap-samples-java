package my.bookshop;

import static cds.gen.my.bookshop.Bookshop_.BOOKS;

import cds.gen.my.bookshop.Books;
import cds.gen.my.bookshop.Reviews;
import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.persistence.PersistenceService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.OptionalDouble;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Takes care of calculating the average rating of a book based on its review
 * ratings.
 */
@Component
public class RatingCalculator {

	private PersistenceService db;

	RatingCalculator(PersistenceService db) {
		this.db = db;
	}

	/**
	 * Initializes the ratings for all existing books based on their reviews.
	 */
	public void initBookRatings() {
		var result = db.run(Select.from(BOOKS).columns(b -> b.ID()));
		for (Books book : result) {
			setBookRating(book.getId());
		}
	}

	/**
	 * Sets the average rating for the given book.
	 *
	 * @param bookId
	 */
	public void setBookRating(String bookId) {
		Result run = db.run(Select.from(BOOKS, b -> b.filter(b.ID().eq(bookId)).reviews()));

		Stream<Double> ratings = run.streamOf(Reviews.class).map(r -> r.getRating().doubleValue());
		BigDecimal rating = getAvgRating(ratings);

		db.run(Update.entity(BOOKS).byId(bookId).data(Books.RATING, rating));
	}

	static BigDecimal getAvgRating(Stream<Double> ratings) {
		OptionalDouble avg = ratings.mapToDouble(Double::doubleValue).average();
		if (!avg.isPresent()) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(avg.getAsDouble()).setScale(1, RoundingMode.HALF_UP);
	}
}
