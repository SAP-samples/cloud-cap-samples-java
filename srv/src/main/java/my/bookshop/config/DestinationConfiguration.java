package my.bookshop.config;

import java.util.Collections;
import java.util.Map;

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
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty;
import com.sap.cloud.sdk.cloudplatform.connectivity.OAuth2DestinationBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;

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

		ServiceBinding uaaBinding = runtime.getEnvironment().getServiceBindings().filter(b -> b.getServiceName().get().equals("xsuaa")).findFirst().get();
		logger.info("UAA Service Binding: {} / {}", uaaBinding.getName().get(), uaaBinding.getServiceName().get());

		Map<String, Object> credentials = uaaBinding.getCredentials();
		Object client = new ClientCredentials((String)credentials.get("clientid"), (String)credentials.get("clientsecret"));
		String tokenUrl = (String)credentials.get("url");

		DestinationAccessor.prependDestinationLoader(
				new DefaultDestinationLoader()
						.registerDestination(
								OAuth2DestinationBuilder
										.forTargetUrl("https://" + applicationUrl + "/")
										.withTokenEndpoint(tokenUrl)
										.withClient((ClientIdentity)client, OnBehalfOf.NAMED_USER_CURRENT_TENANT)
										.withProperties(Collections.singletonMap(DestinationProperty.NAME.getKeyName(), destinationName))
										.build()));
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
