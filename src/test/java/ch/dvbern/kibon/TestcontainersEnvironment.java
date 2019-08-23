package ch.dvbern.kibon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Testcontainers reads the compose-test.yml, starts the docker-compose services and exposes the database and kafka
 * server by overriding the system properties of the application.
 * <p>
 * Use by declaring @QuarkusTestResource(TestcontainersEnvironment.class)
 */
@Testcontainers
public class TestcontainersEnvironment implements QuarkusTestResourceLifecycleManager {

	private static final String DB_SERVICE = "db_1";
	private static final int DB_PORT = 5432;

	private static final String KAFKA_SERVICE = "kafka_1";
	private static final int KAFKA_PORT = 9092;

	@SuppressWarnings("rawtypes")
	@Container
	public static final DockerComposeContainer ENVIRONMENT =
		new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
			.withExposedService(DB_SERVICE, DB_PORT)
			.withExposedService(KAFKA_SERVICE, KAFKA_PORT);

	@Override
	public Map<String, String> start() {
		ENVIRONMENT.start();

		String dbHost = ENVIRONMENT.getServiceHost(DB_SERVICE, DB_PORT);
		Integer dbPort = ENVIRONMENT.getServicePort(DB_SERVICE, DB_PORT);

		String kafkaHost = ENVIRONMENT.getServiceHost(KAFKA_SERVICE, KAFKA_PORT);
		Integer kafkaPort = ENVIRONMENT.getServicePort(KAFKA_SERVICE, KAFKA_PORT);

		Map<String, String> systemProps = new HashMap<>();
		systemProps.put("quarkus.datasource.url", "jdbc:postgresql://" + dbHost + ':' + dbPort + "/kibon-exchange");
		systemProps.put("kafka.bootstrap.servers", kafkaHost + ':' + kafkaPort);

		return systemProps;
	}

	@Override
	public void stop() {
		ENVIRONMENT.stop();
	}
}
