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

package ch.dvbern.kibon.api.verfuegung;

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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@QuarkusTestResource(TestcontainersEnvironment.class)
@QuarkusTest
class VerfuegungenResourceImplTest {

	@Test
	public void testGetAllEndpoint() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("verfuegungen", is(jsonArray(is(not(empty())))))
				.where("institutionen", is(jsonArray(is(not(empty())))))
			));
	}

	@Test
	public void testGetAllEndpointWithAfterIdParam() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen?after_id=100")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("verfuegungen", is(jsonArray(everyItem(jsonObject()
					.where("id", is(jsonInt(greaterThan(100))))))
				))
				.where("institutionen", is(jsonArray(is(not(empty())))))
			));
	}

	@Test
	public void testGetAllEndpointWithLimit() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen?limit=1")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("verfuegungen", is(jsonArray(hasSize(1))))
				.where("institutionen", is(jsonArray(hasSize(1))))
			));
	}

	@Test
	public void testGetAllEndpointLimitMustBeNonnegative() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen?limit=-1")
			.then()
			.assertThat()
			.statusCode(Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	public void testGetAllEndpointRequiresAuthorisation() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen")
			.then()
			.assertThat()
			.statusCode(Status.UNAUTHORIZED.getStatusCode());
	}
}
