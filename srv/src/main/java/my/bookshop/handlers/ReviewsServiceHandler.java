package my.bookshop.handlers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.Row;
import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.adminservice.AdminService_;
import cds.gen.reviewsservice.Reviews;
import cds.gen.reviewsservice.Reviews_;
import cds.gen.reviewsservice.Reviewed;
import cds.gen.reviewsservice.ReviewedContext;
import cds.gen.reviewsservice.ReviewsService_;


@Component
@ServiceName(ReviewsService_.CDS_NAME)
public class ReviewsServiceHandler implements EventHandler {

	private static final Logger logger = LoggerFactory.getLogger(ReviewsServiceHandler.class);

	@Autowired
	@Qualifier(ReviewsService_.CDS_NAME)
	CqnService reviewService;

	@Autowired
	PersistenceService db;

	@Autowired
	@Qualifier(AdminService_.CDS_NAME)
	CqnService adminService;

	@After(event = { CqnService.EVENT_CREATE, CqnService.EVENT_UPSERT, CqnService.EVENT_UPDATE })
	public void afterAddReview(Stream<Reviews> reviews) {
		reviews.forEach(review -> {

			// calculate the average rating of the subject
			List<Row> ratings = reviewService.run(Select.from(Reviews_.CDS_NAME).columns(r -> r.get("rating").asValue()).where(r -> r.get("subject").eq(review.getSubject()))).list();
			Double avg = ratings.stream().mapToDouble(r -> ((Integer) r.get("rating")).doubleValue()).average().orElse(0);

			Reviewed event = Reviewed.create();
			event.setSubject(review.getSubject());
			event.setRating(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));

			ReviewedContext evContext = ReviewedContext.create();
			evContext.setData(event);

			reviewService.emit(evContext);

			logger.info("Review for '{}' with avg rating '{}' was successfully created.", event.getSubject(), event.getRating());
		});
	}
}
