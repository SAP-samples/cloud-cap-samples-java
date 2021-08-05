package my.bookshop.handlers;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;

import cds.gen.reviewservice.ReviewService_;
import cds.gen.reviewservice.Reviews;
import cds.gen.reviewservice.Reviews_;
import my.bookshop.MessageKeys;

@Component
@ServiceName(ReviewService_.CDS_NAME)
public class ReviewServiceHandler implements EventHandler {

	private final DraftService reviewService;
	private final Messages messages;

	ReviewServiceHandler(@Qualifier(ReviewService_.CDS_NAME) DraftService reviewService, Messages messages) {
		this.reviewService = reviewService;
		this.messages = messages;
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
		if (rating == null || rating < 1 || rating > 5) {
			messages.error(MessageKeys.REVIEW_INVALID_RATING).target("in", Reviews_.class, r -> r.rating());
		}
	}

	private void validateBook(Reviews review) {
		if(review.getBookId() == null || reviewService.run(Select.from(ReviewService_.BOOKS).byId(review.getBookId())).rowCount() == 0) {
			messages.error(MessageKeys.BOOK_MISSING).target("in", Reviews_.class, r -> r.book_ID());
		}
	}

}
