package my.bookshop.config;

import com.sap.cds.services.runtime.CdsRuntime;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf;
import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile({ "mocked", "mocked-api-business-partner" })
public class DestinationConfiguration {

	private final static Logger logger = LoggerFactory.getLogger(DestinationConfiguration.class);

	@Autowired
	private Environment environment;

	@Autowired
	private CdsRuntime runtime;

	@EventListener
	void applicationReady(ApplicationReadyEvent ready) {
		Optional<ServiceBinding> binding = runtime.getEnvironment().getServiceBindings()
				.filter(b -> b.getServiceName().get().equals("xsuaa")).findFirst();

		binding.ifPresentOrElse(this::registerCloudDestination, this::registerLocalDestination);
	}

	private void registerCloudDestination(ServiceBinding xsuaaBinding) {
		Integer port = environment.getProperty("local.server.port", Integer.class);
		String destinationName = environment
				.getProperty("cds.remote.services.'[API_BUSINESS_PARTNER]'.destination.name");

		if (port != null && destinationName != null) {
			logger.info("Destination name for mocked API_BUSINESS_PARTNER: {}", destinationName);
			logger.info("UAA Service Binding: {} / {}", xsuaaBinding.getName().get(),
					xsuaaBinding.getServiceName().get());

			// if we run in the cloud, "localhost" will also work to call ourselves
			ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(xsuaaBinding)
					.withOption(
							BtpServiceOptions.AuthenticationServiceOptions.withTargetUri("http://localhost:" + port))
					.onBehalfOf(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT)
					.build();

			HttpDestination destination = ServiceBindingDestinationLoader.defaultLoaderChain().getDestination(options);

			DestinationAccessor.prependDestinationLoader(
					new DefaultDestinationLoader()
							.registerDestination(
									DefaultHttpDestination.fromDestination(destination).name(destinationName).build()));
		}
	}

	private void registerLocalDestination() {
		Integer port = environment.getProperty("local.server.port", Integer.class);
		String destinationName = environment
				.getProperty("cds.remote.services.'[API_BUSINESS_PARTNER]'.destination.name");

		if (port != null && destinationName != null) {
			DefaultHttpDestination httpDestination = DefaultHttpDestination
					.builder("http://localhost:" + port)
					.basicCredentials(new BasicCredentials("authenticated", ""))
					.name(destinationName).build();

			DestinationAccessor.prependDestinationLoader(
					new DefaultDestinationLoader().registerDestination(httpDestination));
		}
	}
}
