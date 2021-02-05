package my.bookshop.handlers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.services.draft.DraftService;

import cds.gen.reviewservice.ReviewService_;
import cds.gen.reviewservice.Reviews;
import cds.gen.reviewservice.Reviews_;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReviewServiceHandlerTest {

	@Resource(name = ReviewService_.CDS_NAME)
	private DraftService reviewService;

	@Test
	@WithMockUser(username = "admin")
	public void testGeneratedId() {
		Result result = reviewService.newDraft(Insert.into(Reviews_.class).entry(Collections.emptyMap()));
		Reviews review = result.single(Reviews.class);
		assertNotNull(review.getId());
	}

}
