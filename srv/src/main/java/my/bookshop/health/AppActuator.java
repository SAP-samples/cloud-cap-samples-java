package my.bookshop.health;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;


/**
 * Custom app actuator implementation.
 */
@Component
@ConditionalOnClass(Endpoint.class)
@Endpoint(id = "bookshop", enableByDefault = true)
public class AppActuator {

	@ReadOperation
	public Map<String, Object> info() {
		Map<String, Object> info = new LinkedHashMap<>();
		info.put("Version", "1.0.0");
		return info;
	}

}
