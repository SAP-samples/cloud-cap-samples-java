package my.bookshop;

import org.springframework.test.context.ActiveProfiles;

/**
 * Runs tests defined in {@link AdminServiceAddressITestBase} with the default and mocked profile.
 * The mocked profile creates a remote services for the API_BUSINESS_PARTNER service (which is however mocked by our own application),
 * so the application behaves as if the AdminService and the API_BUSINESS_PARTNER service were provided by two different applications.
 */
@ActiveProfiles({"default", "mocked"})
public class AdminServiceAddressITest_mocked extends AdminServiceAddressITestBase { }
