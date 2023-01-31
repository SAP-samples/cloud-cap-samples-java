package my.bookshop.graphql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ServiceCatalog;
import com.sap.cds.services.cds.CqnService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.SelectedField;

public class CqnDataFetcher implements DataFetcher<Object> {

	private final CdsModel model;
	private final ServiceCatalog serviceCatalog;

	public CqnDataFetcher(CdsModel model, ServiceCatalog serviceCatalog) {
		this.model = model;
		this.serviceCatalog = serviceCatalog;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		// find the entity, which corresponds to the GraphQL return type
		CdsEntity entity = toEntity(environment.getFieldType());

		// build CQN Select statement
		Select<?> select = Select.from(entity, (ref) -> toPathExpression(ref, environment.getArguments()));
		select.columns(toColumns(environment.getSelectionSet()));

		// execute statement
		Result result = serviceCatalog.getService(CqnService.class, entity.getQualifier()).run(select);

		if(environment.getFieldType() instanceof GraphQLList) {
			// arrayed result expected
			return result.list();
		} else {
			// single result expected
			return result.first().map(r -> (Map<String, Object>) r).orElse(Collections.emptyMap());
		}
	}

	private CdsEntity toEntity(GraphQLOutputType type) {
		String typeName = TypeMappings.toTypeName(type);
		return model.entities().filter(entity -> typeName.equals(TypeMappings.toGraphQLTypeName(entity))).findFirst()
			.orElseThrow(() -> new IllegalStateException("Could not find entity for type name " + typeName));
	}

	private StructuredType<?> toPathExpression(StructuredType<?> ref, Map<String, Object> arguments) {
		// add a filter based on the entity keys to the statement
		CqnPredicate pathCondition = null;
		for(Map.Entry<String, Object> entry : arguments.entrySet()) {
			Predicate keyCondition = CQL.get(entry.getKey()).eq(entry.getValue());
			if(pathCondition == null) {
				pathCondition = keyCondition;
			} else {
				pathCondition = CQL.and(pathCondition, keyCondition);
			}
		}
		return ref.filter(pathCondition);
	}

	private List<CqnSelectListItem> toColumns(DataFetchingFieldSelectionSet selectionSet) {
		List<CqnSelectListItem> items = new ArrayList<>();
		// only get selected fields on current layer
		for(SelectedField field: selectionSet.getFields("*")) {
			if(field.getSelectionSet().getFields().isEmpty()) {
				// no nested selection -> element selection
				items.add(CQL.get(field.getName()));
			} else {
				// nested selections -> expand of association
				items.add(CQL.to(field.getName()).expand(toColumns(field.getSelectionSet())));
			}
		}

		return items;
	}

}
