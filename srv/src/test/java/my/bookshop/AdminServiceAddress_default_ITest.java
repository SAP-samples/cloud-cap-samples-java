package my.bookshop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Runs tests defined in {@link AdminServiceAddressITestBase} with the default profile.
 * The default profile doesn't create any remote services, so the application behaves as if
 * the AdminService and the API_BUSINESS_PARTNER service were provided by the same application.
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles("default")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminServiceAddress_default_ITest extends AdminServiceAddressITestBase {

	@Test
	@Override
	public void testAddressesValueHelp() {
		super.testAddressesValueHelp();
	}

	@Test
	@Override
	public void testOrderWithAddress() throws InterruptedException {
		super.testOrderWithAddress();
	}

}
