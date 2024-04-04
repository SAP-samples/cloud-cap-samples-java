package my.bookshop.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.cds.services.request.FeatureTogglesInfo;
import com.sap.cds.services.request.ParameterInfo;
import com.sap.cds.services.request.UserInfo;
import com.sap.cds.services.runtime.FeatureTogglesInfoProvider;

@Component
@Profile({"cloud", "messaging-cloud"}) // locally, feature toggles are configured directly with mock users
public class CustomFeatureToggleProvider implements FeatureTogglesInfoProvider {

	@Override
	public FeatureTogglesInfo get(UserInfo userInfo, ParameterInfo parameterInfo) {
		if (userInfo.getTenant() == null && userInfo.isSystemUser()) {
			// technical provider user runs with all feature toggles
			return FeatureTogglesInfo.all();
		}

		Map<String, Boolean> toggles = new HashMap<>();
		toggles.put("isbn", userInfo.hasRole("expert"));
		toggles.put("discount", userInfo.hasRole("premium-customer"));
		return FeatureTogglesInfo.create(toggles);
	}
}
