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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.event.Observes;

import com.google.common.collect.ImmutableMap;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers reads the compose-test.yml, starts the docker-compose services and exposes the database and kafka
 * server by overriding the system properties of the application.
 * <p>
 * Use by declaring @QuarkusTestResource(TestcontainersEnvironment.class)
 */
@Testcontainers
public class TestcontainersEnvironment implements QuarkusTestResourceLifecycleManager {

	private static final String CONFLUENT_PLATFORM_VERSION = "5.5.5";
	private static final String KEYCLOAK_VERSION = "15.0.2";

	private static final int KEYCLOAK_PORT = 8080;

	private static final List<String> SCHEMA_REGISTRY_URL_PROPERTIES = Arrays.asList(
		"mp.messaging.incoming.VerfuegungEvents.schema.registry.url",
		"mp.messaging.incoming.InstitutionEvents.schema.registry.url",
		"mp.messaging.incoming.BetreuungAnfrageEvents.schema.registry.url",
		"mp.messaging.outgoing.PlatzbestaetigungBetreuungEvents.schema.registry.url",
		"mp.messaging.outgoing.AnmeldungBestaetigungEvents.schema.registry.url"
	);

	private static AuthzClient authzClient = null;
	private static AuthzClient authzClientFambe = null;
	private static AuthzClient authzClientTagesschule = null;

	@Container
	private final KafkaContainer kafka =
		new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:" + CONFLUENT_PLATFORM_VERSION));

	@Container
	private final SchemaRegistryContainer schemaRegistry = new SchemaRegistryContainer(CONFLUENT_PLATFORM_VERSION);

	@SuppressWarnings("rawtypes")
	@Container
	private final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:11-alpine");

	@Container
	private final KeycloakContainer keycloak = new KeycloakContainer(KEYCLOAK_VERSION);

	@Nonnull
	public static String getAccessToken() {
		return authzClient.obtainAccessToken().getToken();
	}

	@Nonnull
	public static String getFamilyPortalAccessToken() {
		return authzClientFambe.obtainAccessToken().getToken();
	}

	@Nonnull
	public static String getTagesschuleAccessToken() {
		return authzClientTagesschule.obtainAccessToken().getToken();
	}

	@Override
	public Map<String, String> start() {
		String adminPassword = UUID.randomUUID().toString();
		String dummyPassword = "TEST"; // as in src/test/resources/kibon_realm.json

		keycloak
			.withExposedPorts(KEYCLOAK_PORT)
			.withAdminUser("admin")
			.withAdminPassword(adminPassword)
			.start();

		String dbName = "kibon-exchange";
		String dbUser = "kibonExchange";
		String dbPassword = UUID.randomUUID().toString();

		postgres
			.withDatabaseName(dbName)
			.withUsername(dbUser)
			.withPassword(dbPassword)
			.withEnv("TZ", "Europe/Zurich")
			.start();

		Network network = Network.newNetwork();

		kafka
			.withNetwork(network)
			.withNetworkAliases("kafka")
			.start();

		schemaRegistry
			.withNetwork(network)
			.withNetworkAliases("schema-registry")
			.start();

		Map<String, String> systemProps = new HashMap<>();
		systemProps.put("quarkus.datasource.jdbc.url", postgres.getJdbcUrl());
		systemProps.put("quarkus.datasource.username", dbUser);
		systemProps.put("quarkus.datasource.password", dbPassword);
		systemProps.put("kafka.bootstrap.servers", kafka.getBootstrapServers());
		String keycloakURL = "http://" + keycloak.getHost() + ':' + keycloak.getMappedPort(KEYCLOAK_PORT) + "/auth";
		systemProps.put("quarkus.oidc.auth-server-url", keycloakURL + "/realms/kibon");
		systemProps.put("quarkus.oidc.token.issuer", keycloakURL + "/realms/kibon");
		systemProps.put("quarkus.oidc.credentials.secret", dummyPassword);

		SCHEMA_REGISTRY_URL_PROPERTIES.forEach(url -> systemProps.put(url, schemaRegistry.getSchemaRegistryUrl()));

		authzClient = createKeycloakClientConfiguration(keycloakURL, "kitAdmin", dummyPassword);
		authzClientFambe = createKeycloakClientConfiguration(keycloakURL, "fambe", dummyPassword);
		authzClientTagesschule = createKeycloakClientConfiguration(keycloakURL, "tagesschuleTest", dummyPassword);

		return systemProps;
	}

	/**
	 * @see #terminate(Object)
	 */
	@Override
	public void stop() {
	}

	/**
	 * The {@link #stop} method is called before the application is destroyed. If we stop Kafka in {@link #stop}, then
	 * the Vert.X Kafka client keeps trying to reconnect to Kafka until it finally times out (after about 90s).
	 * <p>
	 * Thus we listen to the @Destroyed event to stop Kafka after the application quit.
	 */
	public void terminate(@Observes @Destroyed(ApplicationScoped.class) Object event) {
		schemaRegistry.stop();
		kafka.stop();
		keycloak.stop();
		postgres.stop();
	}

	@Nonnull
	private AuthzClient createKeycloakClientConfiguration(
		@Nonnull String authServerURL,
		@Nonnull String clientId,
		@Nonnull String secret) {

		Map<String, Object> clientSecret = ImmutableMap.of("secret", secret);

		Configuration configuration = new Configuration(authServerURL, "kibon", clientId, clientSecret, null);
		configuration.setVerifyTokenAudience(true);
		configuration.setUseResourceRoleMappings(true);
		configuration.setConfidentialPort(0);

		return AuthzClient.create(configuration);
	}
}
