package my.bookshop.handlers;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${cds.application.services.abp.destination.api-key:}")
	private String apiKey;

	@Before(event = ApplicationLifecycleService.EVENT_APPLICATION_LIFECYCLE_PREPARED)
	public void initializeDestinations() {
		if(apiKey != null && !apiKey.isEmpty()) {
			DefaultHttpDestination httpDestination = DefaultHttpDestination
					.builder("https://sandbox.api.sap.com/s4hanacloud")
					.header("APIKey", apiKey)
					.name("s4-business-partner-api").build();

			DestinationAccessor.prependDestinationLoader(new DefaultDestinationLoader().registerDestination(httpDestination));
		}
	}

}
