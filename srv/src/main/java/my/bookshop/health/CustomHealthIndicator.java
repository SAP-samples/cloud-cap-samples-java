package my.bookshop.health;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator implementation.
 */
@Component("myhealth")
@ConditionalOnEnabledHealthIndicator("myhealth")
public class CustomHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		if (check() != 0) {
			return Health.down().build();
		}
		return Health.up().build();
	}

	private int check() {
		// perform some health check
		return 0;
	}

}
