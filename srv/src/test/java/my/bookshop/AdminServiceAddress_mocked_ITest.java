package my.bookshop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Runs tests defined in {@link AdminServiceAddressITestBase} with the default and mocked profile.
 * The mocked profile creates a remote services for the API_BUSINESS_PARTNER service (which is however mocked by our own application),
 * so the application behaves as if the AdminService and the API_BUSINESS_PARTNER service were provided by two different applications.
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"default", "mocked"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminServiceAddress_mocked_ITest extends AdminServiceAddressITestBase {

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
