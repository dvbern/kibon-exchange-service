/*
 * Copyright (C) 2020 DV Bern AG, Switzerland
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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class SchemaRegistryContainer extends GenericContainer<SchemaRegistryContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(SchemaRegistryContainer.class);

	private static final int SCHEMA_REGISTRY_INTERNAL_PORT = 8081;

	public SchemaRegistryContainer(@Nonnull String confluentPlatformVersion) {
		super("confluentinc/cp-schema-registry:" + confluentPlatformVersion);

		withExposedPorts(SCHEMA_REGISTRY_INTERNAL_PORT)
			.withLogConsumer(new Slf4jLogConsumer(LOG))
			.withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
			.withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:" + SCHEMA_REGISTRY_INTERNAL_PORT)
			.withEnv("SCHEMA_REGISTRY_AVRO_COMPATIBILITY_LEVEL", "full_transitive")
			.waitingFor(Wait.forHttp("/subjects"));
	}

	@Nonnull
	public SchemaRegistryContainer withKafka(@Nonnull KafkaContainer kafkaContainer) {
		String bootstrapServer = "PLAINTEXT://" + kafkaContainer.getNetworkAliases().get(0) + ":9092";
		return withNetwork(kafkaContainer.getNetwork())
			.withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", bootstrapServer);
	}

	@Nonnull
	public String getSchemaRegistryUrl() {
		return "http://" + getContainerIpAddress() + ':' + getMappedPort(SCHEMA_REGISTRY_INTERNAL_PORT);
	}
}
