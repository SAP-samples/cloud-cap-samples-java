package my.bookshop.health;

import org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/** Custom health indicator implementation. */
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
