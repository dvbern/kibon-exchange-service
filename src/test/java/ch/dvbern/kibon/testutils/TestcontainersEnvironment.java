/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.dvbern.kibon.testutils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
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

	private static final String KEYCLOAK_SERVICE = "keycloak_1";
	private static final int KEYCLOAK_PORT = 8080;

	@SuppressWarnings("rawtypes")
	@Container
	public static final DockerComposeContainer ENVIRONMENT =
		new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
			.withExposedService(DB_SERVICE, DB_PORT)
			.withExposedService(KAFKA_SERVICE, KAFKA_PORT)
			.withExposedService(KEYCLOAK_SERVICE, KEYCLOAK_PORT);

	private static AuthzClient authzClient = null;

	@Nonnull
	public static String getAccessToken() {
		return authzClient.obtainAccessToken().getToken();
	}

	@Override
	public Map<String, String> start() {
		ENVIRONMENT.start();

		String dbHost = ENVIRONMENT.getServiceHost(DB_SERVICE, DB_PORT);
		Integer dbPort = ENVIRONMENT.getServicePort(DB_SERVICE, DB_PORT);

		String kafkaHost = ENVIRONMENT.getServiceHost(KAFKA_SERVICE, KAFKA_PORT);
		Integer kafkaPort = ENVIRONMENT.getServicePort(KAFKA_SERVICE, KAFKA_PORT);

		String keycloakHost = ENVIRONMENT.getServiceHost(KEYCLOAK_SERVICE, KEYCLOAK_PORT);
		Integer keycloakPort = ENVIRONMENT.getServicePort(KEYCLOAK_SERVICE, KEYCLOAK_PORT);

		Map<String, String> systemProps = new HashMap<>();
		systemProps.put("quarkus.datasource.url", "jdbc:postgresql://" + dbHost + ':' + dbPort + "/kibon-exchange");
		systemProps.put("kafka.bootstrap.servers", kafkaHost + ':' + kafkaPort);
		String keycloakURL = "http://" + keycloakHost + ':' + keycloakPort + "/auth";
		systemProps.put("quarkus.keycloak.auth-server-url", keycloakURL);

		createKeycloakClientConfiguration(keycloakURL);

		return systemProps;
	}

	@Override
	public void stop() {
		ENVIRONMENT.stop();
	}

	private void createKeycloakClientConfiguration(@Nonnull String authServerURL) {
		Map<String, Object> clientSecret = ImmutableMap.of("secret", "657d6aef-bdc3-40e9-9992-024810d2b24b");

		Configuration configuration = new Configuration(authServerURL, "kibon", "kitAdmin", clientSecret, null);
		configuration.setVerifyTokenAudience(true);
		configuration.setUseResourceRoleMappings(true);
		configuration.setConfidentialPort(0);

		authzClient = AuthzClient.create(configuration);
	}
}
