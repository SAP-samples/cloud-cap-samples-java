package my.bookshop.handlers;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.Row;
import com.sap.cds.ql.Select;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.catalogservice.CatalogService_;
import cds.gen.my.bookshop.Reviews;
import cds.gen.my.bookshop.Reviews_;
import cds.gen.reviewservice.ReviewService_;
import my.bookshop.MessageKeys;

@Component
@ServiceName(ReviewService_.CDS_NAME)
public class ReviewServiceHandler implements EventHandler {

	private CdsService catalogService;

	ReviewServiceHandler(@Qualifier(CatalogService_.CDS_NAME) CdsService catalogService) {
		this.catalogService = catalogService;
	}

	@Before(event = { CdsService.EVENT_CREATE, CdsService.EVENT_UPDATE })
	public void beforeAddReview(Stream<Reviews> reviews) {
		reviews.forEach(review -> {
			validateBook(review);
			validateRating(review);
		});
	}

	private void validateRating(Reviews review) {
		Integer rating = review.getRating();
		if (rating < 0 || rating > 5) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.REVIEW_INVALID_RATING)
					.messageTarget(Reviews_.class, r -> r.rating());
		}
	}

	private void validateBook(Reviews review) {
		if (review.getBookId() == null) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.BOOK_MISSING)
					.messageTarget(Reviews_.class, r -> r.book_ID());
		}

		Optional<Row> row = catalogService.run(Select.from(CatalogService_.BOOKS).byId(review.getBookId())).first();
		row.orElseThrow(() -> new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.BOOK_MISSING)
				.messageTarget(Reviews_.class, r -> r.book_ID()));
	}

}
