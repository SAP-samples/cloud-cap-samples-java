package my.bookshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.draft.DraftService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Authors;
import cds.gen.adminservice.Authors_;
import cds.gen.adminservice.Orders;
import cds.gen.adminservice.Orders_;

@ExtendWith(SpringExtension.class)
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

	@Test
	@WithMockUser(username = "user")
	public void testUnauthorizedAccess() {
		assertThrows(ServiceException.class, () -> {
			adminService.newDraft(Insert.into(Orders_.class).entry(Collections.emptyMap()));
		});
	}

	@Test
	@WithMockUser(username = "admin")
	public void testInvalidAuthorName() {
		assertThrows(ServiceException.class, () -> {
			Authors author = Authors.create();
			author.setName("little Joey");
			adminService.run(Insert.into(Authors_.class).entry(author));
		});
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
