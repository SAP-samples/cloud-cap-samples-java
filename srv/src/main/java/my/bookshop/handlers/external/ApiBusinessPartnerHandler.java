package my.bookshop.handlers.external;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnLiteral;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.ql.cqn.CqnValue;
import com.sap.cds.ql.cqn.CqnVisitor;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odatav4.client.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odatav4.client.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odatav4.exception.ODataServiceException;

import cds.gen.api_business_partner.ABusinessPartnerAddress_;
import cds.gen.api_business_partner.ApiBusinessPartner_;

/**
 * This class implements the connection to an S4 Business Partner API
 * In the future CAP will be able to provide this out-of-the-box.
 *
 * As we only need the A_BusinessPartnerAddress entity in this application,
 * this is the only entity we implement in this handler.
 */
@Component
@ServiceName(ApiBusinessPartner_.CDS_NAME)
public class ApiBusinessPartnerHandler implements EventHandler {

	@Value("${cds.services.abp.destination:}")
	private String destination;

	@On(entity = ABusinessPartnerAddress_.CDS_NAME)
	@SuppressWarnings("unchecked")
	public void readBusinessPartnerAddresses(CdsReadEventContext context) {
		if(destination == null || destination.trim().isEmpty()) {
			return;
		}

		List<String> queryParams = new ArrayList<>();

		// get columns from CQN
		List<CqnSelectListItem> cqnColumns = context.getCqn().columns();
		boolean hasStar = cqnColumns.stream().anyMatch(c -> c.toJson().equals("\"*\""));
		if (!hasStar && !cqnColumns.isEmpty()) {
			String select = "$select=" + cqnColumns.stream().map(i -> i.displayName()).collect(Collectors.joining(","));
			queryParams.add(select);
		}

		// add where filters from CQN
		List<String> whereFilters = new ArrayList<>();
		context.getCqn().where().ifPresent(w -> w.accept(new CqnVisitor() {
			@Override
			public void visit(com.sap.cds.ql.cqn.CqnComparisonPredicate cqnComparisonPredicate) {
				CqnValue left = cqnComparisonPredicate.left();
				CqnValue right = cqnComparisonPredicate.right();
				String operator = cqnComparisonPredicate.operator().name().toLowerCase(Locale.ENGLISH);
				StringBuilder whereFilter = new StringBuilder();
				if (left instanceof CqnElementRef && right instanceof CqnLiteral) {
					whereFilter
						.append(((CqnElementRef) left).displayName()).append(" ")
						.append(operator)
						.append(" '").append(((CqnLiteral<?>) right).value()).append("'");
				} else if (right instanceof CqnElementRef && left instanceof CqnLiteral) {
					whereFilter
						.append(((CqnElementRef) right).displayName()).append(" ")
						.append(operator)
						.append(" '").append(((CqnLiteral<?>) left).value()).append("'");
				}
				if(whereFilter.length() > 0) {
					whereFilters.add(whereFilter.toString());
				}
			};
		}));

		if (!whereFilters.isEmpty()) {
			String filter = "$filter=" + whereFilters.stream().collect(Collectors.joining(" and "));
			queryParams.add(filter);
		}

		HttpClient client = HttpClientAccessor.getHttpClient(DestinationAccessor.getDestination(destination).asHttp());
		ODataRequestRead readRequest = new ODataRequestRead("sap/opu/odata/sap/" + ApiBusinessPartner_.CDS_NAME, "A_BusinessPartnerAddress", queryParams.stream().collect(Collectors.joining("&")));

		try {
			ODataRequestResultGeneric result = readRequest.execute(client);
			Map<String, Object> resultMap = result.asMap();
			List<Map<String, Object>> resultList = (List<Map<String, Object>>) ((Map<String, Object>) resultMap.get("d")).get("results");
			resultList.forEach(m -> m.remove("__metadata"));
			context.setResult(resultList);
		} catch (ODataServiceException e) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Could not query BusinessPartner API", e);
		}
	}

}
