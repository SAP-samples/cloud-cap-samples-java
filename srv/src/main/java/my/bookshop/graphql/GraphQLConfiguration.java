package my.bookshop.graphql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.runtime.CdsRuntime;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

@Configuration
public class GraphQLConfiguration {

    @Bean
    public GraphQL graphQL(CdsRuntime runtime, CdsModel model) {
		// create schema from CDS model
		String schema = new GraphQLSchemaGenerator(model).generate();
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);

		// create runtime wiring with default CQN-based data fetcher for all queries
		RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
			.type(TypeRuntimeWiring.newTypeWiring("Query").defaultDataFetcher(new CqnDataFetcher(model, runtime.getServiceCatalog())))
			.build();

        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
	}

}
