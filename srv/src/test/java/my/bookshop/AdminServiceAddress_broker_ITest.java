package my.bookshop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = "cds.remote.services.'[API_BUSINESS_PARTNER]'.destination.name=myself-AdminServiceAddressITest")
public class AdminServiceAddress_broker_ITest extends AdminServiceAddressITestBase {
	@Test
	@Override
	@EnabledIf(value = "#{'${spring.profiles.active}' == 'kafka-messaging-cloud'}", loadContext = true)
	public void testOrderWithAddress() throws InterruptedException {
		super.testOrderWithAddress();
	}
}
