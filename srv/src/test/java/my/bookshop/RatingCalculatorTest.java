package my.bookshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class RatingCalculatorTest {

	/*
	 * Holder class for a book rating calculation test case.
	 */
	private class RatingTestFixture {
		Stream<Double> ratings;
		double expectedAvg;

		RatingTestFixture(Stream<Double> ratings, double expectedAvg) {
			this.ratings = ratings;
			this.expectedAvg = expectedAvg;
		}
	}

	@Test
	public void testGetAvgRating() {
		RatingTestFixture f1 = new RatingTestFixture(Stream.of(1.0, 2.0, 3.0, 4.0, 5.0), 3.0);
		RatingTestFixture f2 = new RatingTestFixture(Stream.of(1.3, 2.4, 3.5, 4.9, 5.1), 3.4);
		RatingTestFixture f3 = new RatingTestFixture(Stream.of(2.1, 4.0, 2.7, 3.8, 4.9), 3.5);

		Stream.of(f1, f2, f3).forEach(f -> {
			BigDecimal avgRating = RatingCalculator.getAvgRating(f.ratings);
			assertEquals(f.expectedAvg, avgRating.doubleValue());
		});
	}

}
