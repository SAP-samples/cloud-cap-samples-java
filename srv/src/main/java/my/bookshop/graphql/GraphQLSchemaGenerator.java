package my.bookshop.graphql;

import java.util.ArrayList;
import java.util.List;

import com.sap.cds.reflect.CdsAssociationType;
import com.sap.cds.reflect.CdsElement;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.reflect.CdsService;

public class GraphQLSchemaGenerator {

	private static final String NL = "\n";
	private static final String T = "\t";
	private final CdsModel model;
	private final StringBuilder builder = new StringBuilder();
	private final List<String> queries = new ArrayList<>();

	public GraphQLSchemaGenerator(CdsModel model) {
		this.model = model;
	}

	public String generate() {
		// generate types from all service entities
		model.services().flatMap(CdsService::entities).forEach(this::generateEntity);

		// generate GraphQL's special Query type
		builder.append("type Query {").append(NL);
		queries.forEach(query -> builder.append(T).append(query).append(NL));
		builder.append("}").append(NL);

		return builder.toString();
	}

	private void generateEntity(CdsEntity entity) {
		builder.append("type ").append(TypeMappings.toGraphQLTypeName(entity)).append(" {").append(NL);
		entity.elements()
			.filter(e -> isExposed(e, entity.getQualifier()))
			.forEach(this::generateEntityElement);
		builder.append("}").append(NL);

		// add queries for each exposed entity
		if(!entity.findAnnotation("cds.autoexposed").map(a -> (Boolean) a.getValue()).orElse(false) && !entity.getName().endsWith("_drafts")) {
			// byId query
			if(entity.keyElements().count() > 0) {
				queries.add(generateByIdQuery(entity));
			}
			queries.add(generateAllQuery(entity));
		}
	}

	private boolean isExposed(CdsElement element, String serviceName) {
		if(element.getType().isAssociation() && !element.getType().as(CdsAssociationType.class).getTarget().getQualifier().equals(serviceName)) {
			return false;
		}
		return true;
	}

	private void generateEntityElement(CdsElement element) {
		if(element.getName().equals("DraftAdministrativeData")) {
			return;
		}

		builder.append(T);
		generateElement(element, builder);
		builder.append(NL);
	}

	private void generateElement(CdsElement element, StringBuilder builder) {
		builder.append(element.getName()).append(": ").append(TypeMappings.toGraphQLTypeName(element.getType()));
	}

	private String generateByIdQuery(CdsEntity entity) {
		StringBuilder queryBuilder = new StringBuilder();
		// get<Entity>ById(<Keys>): <Entity>
		queryBuilder.append("get").append(entity.getQualifiedName().replace(".", "")).append("ById(");

		// generate arguments based on the entity keys
		entity.keyElements().forEach(key -> {
			generateElement(key, queryBuilder);
			queryBuilder.append(", ");
		});
		queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());

		queryBuilder.append("): ").append(TypeMappings.toGraphQLTypeName(entity));
		return queryBuilder.toString();
	}

	private String generateAllQuery(CdsEntity entity) {
		// get<Entity>: [<Entity>]
		return new StringBuilder()
			.append("get").append(entity.getQualifiedName().replace(".", ""))
			.append(": [").append(TypeMappings.toGraphQLTypeName(entity)).append("]")
			.toString();
	}

}
