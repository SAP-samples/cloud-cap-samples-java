package my.bookshop;

import java.util.Collections;

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
		if (userInfo.hasRole("ft_isbn")) {
			return FeatureTogglesInfo.create(Collections.singletonMap("isbn", true));
		}
		return FeatureTogglesInfo.none();
	}
}
