package my.bookshop.config;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.sap.cds.services.runtime.CdsRuntime;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

@Component
@Profile({"mocked", "mocked-api-business-partner"})
public class DestinationConfiguration {

	private final static Logger logger = LoggerFactory.getLogger(DestinationConfiguration.class);

	@Autowired
	private Environment environment;

	@Autowired
	private CdsRuntime runtime;

	@EventListener
	void applicationReady(ApplicationReadyEvent ready) {
		String applicationUrl = runtime.getEnvironment().getApplicationInfo().getUrl();

		if (applicationUrl != null) {
			// it seems we're running in the cloud
			registerCloudDestination(applicationUrl);
		} else {
			registerLocalDestination();
		}
	}

	private void registerCloudDestination(String applicationUrl) {
		String destinationName = environment.getProperty("cds.remote.services.'[API_BUSINESS_PARTNER]'.destination.name");

		logger.info("TEST BINDINGS: {}", runtime.getEnvironment().getServiceBindings().map(ServiceBinding::getName).collect(Collectors.toList()));
		//DestinationResolver.getDestinationForXsuaaBasedServiceBinding(applicationUrl, null, OnBehalfOf.NAMED_USER_CURRENT_TENANT);
	}

	private void registerLocalDestination() {
		Integer port = environment.getProperty("local.server.port", Integer.class);
		String destinationName = environment.getProperty("cds.remote.services.'[API_BUSINESS_PARTNER]'.destination.name");

		if(port != null && destinationName != null) {
			DefaultHttpDestination httpDestination = DefaultHttpDestination
					.builder("http://localhost:" + port)
					.basicCredentials(new BasicCredentials("authenticated", ""))
					.name(destinationName).build();

			DestinationAccessor.prependDestinationLoader(
					new DefaultDestinationLoader().registerDestination(httpDestination));
		}
	}
}
