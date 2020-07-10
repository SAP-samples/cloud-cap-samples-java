package my.bookshop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.draft.DraftService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Authors_;
import cds.gen.adminservice.Orders;
import cds.gen.adminservice.Orders_;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminServiceTest {

	@Resource(name = AdminService_.CDS_NAME)
	private DraftService adminService;

	@Autowired
	private MockMvc mockMvc;

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
		Map<String, Object> data = new HashMap<>();
		data.put("name", "little Joey");
		adminService.run(Insert.into(Authors_.class).entry(data));
	}

	@Test
	@WithMockUser(username = "admin")
	public void testValidAuthorName() {
		Map<String, Object> data = new HashMap<>();
		data.put("name", "Big Joey");
		Result result = adminService.run(Insert.into(Authors_.class).entry(data));
		assertEquals(1, result.rowCount());
	}

}
