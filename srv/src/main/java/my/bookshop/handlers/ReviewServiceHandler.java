package my.bookshop.handlers;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.Row;
import com.sap.cds.ql.Select;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.reviewservice.ReviewService_;
import cds.gen.reviewservice.Reviews;
import cds.gen.reviewservice.Reviews_;
import my.bookshop.MessageKeys;

@Component
@ServiceName(ReviewService_.CDS_NAME)
public class ReviewServiceHandler implements EventHandler {

	private DraftService reviewService;

	ReviewServiceHandler(@Qualifier(ReviewService_.CDS_NAME) DraftService reviewService) {
		this.reviewService = reviewService;
	}

	@Before(event = { CqnService.EVENT_CREATE, CqnService.EVENT_UPSERT, CqnService.EVENT_UPDATE })
	public void beforeAddReview(Stream<Reviews> reviews) {
		reviews.forEach(review -> {
			validateBook(review);
			validateRating(review);
		});
	}

	private void validateRating(Reviews review) {
		Integer rating = review.getRating();
		if (rating < 1 || rating > 5) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.REVIEW_INVALID_RATING)
			.messageTarget(Reviews_.class, r -> r.rating());
		}
	}

	private void validateBook(Reviews review) {
		if (review.getBookId() == null) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.BOOK_MISSING)
			.messageTarget(Reviews_.class, r -> r.book_ID());
		}
		Optional<Row> row = reviewService.run(Select.from(ReviewService_.BOOKS).byId(review.getBookId())).first();
		row.orElseThrow(() -> new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.BOOK_MISSING)
				.messageTarget(Reviews_.class, r -> r.book_ID()));
	}

}
