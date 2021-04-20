package my.bookshop.handlers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.services.application.ApplicationLifecycleService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;

import my.bookshop.RatingCalculator;

/**
 * Initializes the book ratings based on their review ratings.
 */
@Component
@Profile("default")
@ServiceName(ApplicationLifecycleService.DEFAULT_NAME)
public class BookRatingInitialization implements EventHandler {

	private RatingCalculator ratingCalculator;

	BookRatingInitialization(RatingCalculator ratingCalculator) {
		this.ratingCalculator = ratingCalculator;
	}

	@After(event = ApplicationLifecycleService.EVENT_APPLICATION_PREPARED)
	public void initBookRatings() {
		this.ratingCalculator.initBookRatings();
	}
}
