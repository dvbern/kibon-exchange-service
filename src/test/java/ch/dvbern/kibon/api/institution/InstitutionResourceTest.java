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

package ch.dvbern.kibon.api.institution;

import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.testutils.TestcontainersEnvironment;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.jackson.JsonMatchers.isJsonStringMatching;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonText;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTestResource(TestcontainersEnvironment.class)
@QuarkusTest
class InstitutionResourceTest {

	@Test
	void testGetInstitutionById() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/institutions/1")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("id", is(jsonText(equalTo("1"))))
			));
	}

	@Test
	void testGetInstitutionByIdWithUnknownID() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/institutions/unknown")
			.then()
			.assertThat()
			.statusCode(Status.NOT_FOUND.getStatusCode());
	}

	@Test
	void testGetInstitutionByIdWithoutActiveClientPermission() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/institutions/3")
			.then()
			.assertThat()
			// The Institution & client exist, FORBIDDEN shall be returned when the client is no longer
			// permitted to access the institution.
			.statusCode(Status.FORBIDDEN.getStatusCode());
	}

	@Test
	void testGetInstitutionByIdWithoutClientPermission() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/institutions/4")
			.then()
			.assertThat()
			// Even though the Institution actually exists, NOT_FOUND shall be returned when the client lacks
			// permission
			.statusCode(Status.NOT_FOUND.getStatusCode());
	}
}
