package my.bookshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class DatabaseConfiguration {
	private static final String POSTGRES = "postgres";
	private static final Logger postgresLogger = LoggerFactory.getLogger(POSTGRES);

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer(@Value("${my.bookshop.postgres-image}") String imageName) {
		DockerImageName image = DockerImageName.parse(imageName).asCompatibleSubstituteFor(POSTGRES);
		return new PostgreSQLContainer<>(image)
			.withLogConsumer(outputFrame -> postgresLogger.info(outputFrame.getUtf8StringWithoutLineEnding()));
	}

}
