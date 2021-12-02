package my.bookshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;

@Component
public class DestinationConfiguration {

	@Autowired
	private Environment environment;

	@EventListener
	void applicationReady(ApplicationReadyEvent ready) {
		Integer port = environment.getProperty("local.server.port", Integer.class);
		String destinationName = environment.getProperty("cds.remote.services.'[API_BUSINESS_PARTNER]'.destination.name");
		if(port != null && destinationName != null) {
			DefaultHttpDestination httpDestination = DefaultHttpDestination
			.builder("http://localhost:" + port)
			.name(destinationName).build();

			DestinationAccessor.prependDestinationLoader(
				new DefaultDestinationLoader().registerDestination(httpDestination));
		}
	}

}
