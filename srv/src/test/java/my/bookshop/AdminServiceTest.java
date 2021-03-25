package my.bookshop;

import static cds.gen.adminservice.AdminService_.AUTHORS;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminServiceTest {

	@Resource(name = AdminService_.CDS_NAME)
	private DraftService adminService;

	@Test
	@WithMockUser(username = "user")
	public void testUnauthorizedAccess() {
		assertThrows(ServiceException.class, () -> {
			adminService.newDraft(Insert.into(AUTHORS).entry(Collections.emptyMap()));
		});
	}

	@Test
	@WithMockUser(username = "admin")
	public void testInvalidAuthorName() {
		assertThrows(ServiceException.class, () -> {
			Authors author = Authors.create();
			author.setName("little Joey");
			adminService.run(Insert.into(AUTHORS).entry(author));
		});
	}

	@Test
	@WithMockUser(username = "admin")
	public void testValidAuthorName() {
		Authors author = Authors.create();
		author.setName("Big Joey");
		Result result = adminService.run(Insert.into(AUTHORS).entry(author));
		assertEquals(1, result.rowCount());
	}

}
