package my.bookshop.graphql;

import com.sap.cds.reflect.CdsAssociationType;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsSimpleType;
import com.sap.cds.reflect.CdsType;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;

public class TypeMappings {

	private TypeMappings() {
		// hidden
	}

	public static String toTypeName(GraphQLType type) {
		if(type instanceof GraphQLObjectType) {
			return ((GraphQLObjectType) type).getName();
		} else if (type instanceof GraphQLList) {
			return toTypeName(((GraphQLList) type).getWrappedType());
		}

		throw new UnsupportedOperationException("Unsupported type " + type);
	}

	public static String toGraphQLTypeName(CdsType type) {
		if(type.isSimple()) {
			// map standard CDS types to default GraphQL scalar types
			switch(type.as(CdsSimpleType.class).getType()) {
				case UUID:
					return "ID";
				case BOOLEAN:
					return "Boolean";
				case INTEGER:
					return "Int";
				case DECIMAL:
				case DOUBLE:
					return "Float";
				default:
					return "String";
			}
		} else if (type.isAssociation()) {
			CdsAssociationType association = type.as(CdsAssociationType.class);
			String entityType = toGraphQLTypeName(association.getTarget());
			String targetMax = association.getCardinality().getTargetMax();
			if(targetMax.equals("*") || Integer.parseInt(targetMax) > 1) {
				// toMany association -> arrayed type
				return "[" + entityType + "]";
			} else {
				// toOne association
				return entityType;
			}
		} else if (type.isStructured()) {
			if(type instanceof CdsEntity) {
				// GraphQL does not allow '.' in types
				return type.as(CdsEntity.class).getQualifiedName().replace('.', '_');
			}
		}

		throw new UnsupportedOperationException("Unsupported type " + type);
	}

}
