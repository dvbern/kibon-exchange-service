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
import ch.dvbern.kibon.util.ConstantsUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.jackson.IsJsonText.jsonText;
import static com.spotify.hamcrest.jackson.JsonMatchers.isJsonStringMatching;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonArray;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonInt;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@QuarkusTestResource(TestcontainersEnvironment.class)
@QuarkusTest
class VerfuegungenResourceQuarkusTest {

	@Test
	public void testGetAllEndpoint() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/verfuegungen")
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
			.get("/verfuegungen?after_id=100")
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
			.get("/verfuegungen?limit=1")
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
			.get("/verfuegungen?limit=-1")
			.then()
			.assertThat()
			.statusCode(Status.BAD_REQUEST.getStatusCode());
	}

		@Test
	public void testGetAllEndpointMustNotBeLargerThanMaxLimit() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/verfuegungen?limit=" + (ConstantsUtil.MAX_LIMIT + 1))
			.then()
			.assertThat()
			.statusCode(Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	public void testGetAllEndpointRequiresAuthorisation() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("/verfuegungen")
			.then()
			.assertThat()
			.statusCode(Status.UNAUTHORIZED.getStatusCode());
	}

	/**
	 * The test setup is based on import-test.sql:
	 * - There should be 300 Verfuegungen (without Zeitabschnitte) with unlimited gueltigkeit
	 * - There should be 100 Verfuegungen with 12 Zeitabschnitte each, for months Aug 2020 to July 2021.
	 * - Since client is only allowed access from 2021-01-01, all (existing) Zeitabschnitte must be for 2021.
	 */
	@Test
	public void testGetAllFiltersByGueltigkeit() {
		Matcher<JsonNode> zeitabschnittInsideGueltigkeit = jsonObject()
			.where("von", is(jsonText(containsString("2021-"))))
			.where("bis", is(jsonText(containsString("2021-"))));

		Matcher<JsonNode> zeitabschnitteInGueltigkeit = jsonArray(everyItem(zeitabschnittInsideGueltigkeit));

		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/verfuegungen")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("verfuegungen", is(jsonArray(everyItem(jsonObject()
					.where("zeitabschnitte", zeitabschnitteInGueltigkeit)
					.where("ignorierteZeitabschnitte", zeitabschnitteInGueltigkeit)
				))))));
	}
}
