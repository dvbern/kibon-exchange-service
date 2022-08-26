/*
 * Copyright (C) 2022 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.api.dashboard;

import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.testutils.TestcontainersEnvironment;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.jackson.JsonMatchers.isJsonStringMatching;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonArray;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonInt;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@QuarkusTestResource(TestcontainersEnvironment.class)
@QuarkusTest
public class DashboardResourceTest {

	@Test
	void testGetGemeindeRequiresDashboardRole() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/dashboard/gemeinden")
			.then()
			.assertThat()
			.statusCode(Status.FORBIDDEN.getStatusCode());
	}

	@Test
	void testGetAllGemeinden() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getDashboardAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/dashboard/gemeinden")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("gemeinden", is(jsonArray(
					everyItem(jsonObject()
						.where("id", is(jsonInt(1)))
					)
				)))
			));
	}

	@Test
	void testGetGemeindenKennzahlenRequiresDashboardRole() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/dashboard/gemeinden-kennzahlen")
			.then()
			.assertThat()
			.statusCode(Status.FORBIDDEN.getStatusCode());
	}

	@Test
	void testGetAllGemeindenKennzahlen() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getDashboardAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/dashboard/gemeinden-kennzahlen")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("gemeindenKennzahlen", is(jsonArray(
					everyItem(jsonObject()
						.where("id", is(jsonInt(1)))
					)
				)))
			));
	}

	@Test
	void testGetInstitutionenRequiresDashboardRole() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/dashboard/institutionen")
			.then()
			.assertThat()
			.statusCode(Status.FORBIDDEN.getStatusCode());
	}

	@Test
	void testGetAllInstitutionen() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getDashboardAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/dashboard/institutionen")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("institutionen", is(jsonArray(is(not(empty())))
				))
			));
	}
}
