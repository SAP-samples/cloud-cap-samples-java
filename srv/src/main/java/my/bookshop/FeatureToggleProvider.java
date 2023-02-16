package my.bookshop;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.services.request.FeatureTogglesInfo;
import com.sap.cds.services.request.ParameterInfo;
import com.sap.cds.services.request.UserInfo;
import com.sap.cds.services.runtime.FeatureTogglesInfoProvider;

// When application executed locally, the toggles can be switched via application.yaml
@Component
@Profile("cloud")
public class FeatureToggleProvider implements FeatureTogglesInfoProvider {

	@Override
	public FeatureTogglesInfo get(UserInfo userInfo, ParameterInfo parameterInfo) {
		Map<String, Boolean> toggles = new HashMap<>();
		toggles.put("isbn", userInfo.hasRole("expert"));
		toggles.put("discount", userInfo.hasRole("premium-customer"));
		return FeatureTogglesInfo.create(toggles);
	}
}
