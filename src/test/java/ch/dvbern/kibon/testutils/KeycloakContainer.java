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

import org.testcontainers.containers.GenericContainer;

import static org.testcontainers.containers.BindMode.READ_ONLY;

/**
 * The kibon_realm.json contains a default keycloak configuration, with some pre-configured clients:
 * <ul>
 *     <li>kibon-exchange-service - used by the quarkus application to verify tokens</li>
 *     <li>kitAdmin - simulates an external client with role `user`</li>
 *     <li>fambe - simulates an external client with role `familyportal`</li>
 * </ul>
 */
public class KeycloakContainer extends GenericContainer<KeycloakContainer> {

	public KeycloakContainer(@Nonnull String keycloakVersion) {
		super("docker-registry.dvbern.ch/dockerhub/jboss/keycloak:" + keycloakVersion);

		String realPath = "/tmp/kibon_realm.json";

		withClasspathResourceMapping("kibon_realm.json", realPath, READ_ONLY)
			.withEnv("TZ", "Europe/Zurich")
			.withEnv("KEYCLOAK_IMPORT", realPath)
			.withCommand("-Dkeycloak.profile.feature.upload_scripts=enabled")
		;
	}

	@Nonnull
	public KeycloakContainer withAdminUser(@Nonnull String adminUser) {
		return withEnv("KEYCLOAK_USER", adminUser);
	}

	@Nonnull
	public KeycloakContainer withAdminPassword(@Nonnull String adminPassword) {
		return withEnv("KEYCLOAK_PASSWORD", adminPassword);
	}
}
