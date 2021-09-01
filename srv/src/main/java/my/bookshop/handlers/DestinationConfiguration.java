package my.bookshop.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.sap.cds.services.application.ApplicationLifecycleService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;

@Component
@ServiceName(ApplicationLifecycleService.DEFAULT_NAME)
public class DestinationConfiguration implements EventHandler {

	@Value("${api-hub.api-key:}")
	private String apiKey;

	@Autowired
	private Environment environment;

	@Before(event = ApplicationLifecycleService.EVENT_APPLICATION_PREPARED)
	void initializeDestinations() {
		if(apiKey != null && !apiKey.isEmpty()) {
			DefaultHttpDestination httpDestination = DefaultHttpDestination
					.builder("https://sandbox.api.sap.com/s4hanacloud")
					.header("APIKey", apiKey)
					.name("s4-business-partner-api").build();

			DestinationAccessor.prependDestinationLoader(
				new DefaultDestinationLoader().registerDestination(httpDestination));
		}
	}

	@EventListener
	void applicationReady(ApplicationReadyEvent ready) {
		Integer port = environment.getProperty("local.server.port", Integer.class);
		if(port != null) {
			DefaultHttpDestination httpDestination = DefaultHttpDestination
			.builder("http://localhost:" + port)
			.name("myself").build();

			DestinationAccessor.prependDestinationLoader(
				new DefaultDestinationLoader().registerDestination(httpDestination));
		}
	}

}
