package my.bookshop;

import org.springframework.test.context.ActiveProfiles;

/**
 * Runs tests defined in {@link AdminServiceAddressITestBase} with the default profile.
 * The default profile doesn't create any remote services, so the application behaves as if
 * the AdminService and the API_BUSINESS_PARTNER service were provided by the same application.
 */
@ActiveProfiles("default")
public class AdminServiceAddressITest_default extends AdminServiceAddressITestBase { }
