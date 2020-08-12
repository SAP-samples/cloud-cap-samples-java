package my.bookshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.draft.DraftService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Authors;
import cds.gen.adminservice.Authors_;
import cds.gen.adminservice.Orders;
import cds.gen.adminservice.Orders_;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminServiceTest {

	@Resource(name = AdminService_.CDS_NAME)
	private DraftService adminService;

	@Test
	@WithMockUser(username = "admin")
	public void testGeneratedId() {
		Result result = adminService.newDraft(Insert.into(Orders_.class).entry(Collections.emptyMap()));
		Orders order = result.single(Orders.class);
		assertNotNull(order.getId());
	}

	@Test(expected = ServiceException.class)
	@WithMockUser(username = "user")
	public void testUnauthorizedAccess() {
		adminService.newDraft(Insert.into(Orders_.class).entry(Collections.emptyMap()));
	}

	@Test(expected = ServiceException.class)
	@WithMockUser(username = "admin")
	public void testInvalidAuthorName() {
		Authors author = Authors.create();
		author.setName("little Joey");
		adminService.run(Insert.into(Authors_.class).entry(author));
	}

	@Test
	@WithMockUser(username = "admin")
	public void testValidAuthorName() {
		Authors author = Authors.create();
		author.setName("Big Joey");
		Result result = adminService.run(Insert.into(Authors_.class).entry(author));
		assertEquals(1, result.rowCount());
	}

}
