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

package ch.dvbern.kibon.api.platzbestaetigung;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.exchange.api.common.platzbestaetigung.BetreuungDTO;
import ch.dvbern.kibon.exchange.api.common.platzbestaetigung.BetreuungZeitabschnittDTO;
import ch.dvbern.kibon.exchange.api.common.shared.Zeiteinheit;
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
class PlatzbestaetigungResourceTest {

	//	@SuppressWarnings("checkstyle:VisibilityModifier")
	//	@Inject
	//	ObjectMapper objectMapper;

	@Test
	void testGetAllEndpoint() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/platzbestaetigung")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("anfragen", is(jsonArray(is(not(empty())))))
			));
	}

	@Test
	void testGetAllEndpointWithAfterIdParam() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/platzbestaetigung?after_id=20")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("anfragen", is(jsonArray(everyItem(jsonObject()
					.where("id", is(jsonInt(greaterThan(20))))))
				))
			));
	}

	@Test
	void testGetAllEndpointWithLimit() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/platzbestaetigung?limit=1")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("anfragen", is(jsonArray(hasSize(1))))
			));
	}

	@Test
	void testGetAllEndpointLimitMustBeNonnegative() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/platzbestaetigung?limit=-1")
			.then()
			.assertThat()
			.statusCode(Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	void testGetAllEndpointRequiresAuthorisation() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("/platzbestaetigung")
			.then()
			.assertThat()
			.statusCode(Status.UNAUTHORIZED.getStatusCode());
	}

	@Test
	void testPostBetreuungRequiresAuthorisation() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.post("/platzbestaetigung/betreuung")
			.then()
			.assertThat()
			.statusCode(Status.UNAUTHORIZED.getStatusCode());
	}

	@Test
	void testPostBetreuungRequiresValidDTO() {
		BetreuungDTO invalid = create(createInvalidZeitabschitt());

		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.body(invalid)
			.when()
			.post("/platzbestaetigung/betreuung")
			.then()
			.assertThat()
			.statusCode(Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	void testPostBetreuungAcceptsValidDTO() {
		BetreuungDTO valid = create(createValidZeitabschitt());

		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.body(valid)
			.when()
			.post("/platzbestaetigung/betreuung")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode());
	}

	@Nonnull
	private BetreuungDTO create(@Nonnull BetreuungZeitabschnittDTO zeitabschnitt) {
		BetreuungDTO dto = new BetreuungDTO();
		dto.setInstitutionId("1");
		dto.setRefnr("1.1.1.1");
		dto.getZeitabschnitte().add(zeitabschnitt);

		return dto;
	}

	@Nonnull
	private BetreuungZeitabschnittDTO createValidZeitabschitt() {
		BetreuungZeitabschnittDTO valid = new BetreuungZeitabschnittDTO();
		valid.setBetreuungspensum(BigDecimal.valueOf(40));
		valid.setBetreuungskosten(BigDecimal.valueOf(1234));
		valid.setVon(LocalDate.of(2019, 1, 8));
		valid.setBis((LocalDate.of(2020, 7, 31)));
		valid.setPensumUnit(Zeiteinheit.PERCENTAGE);

		return valid;
	}

	@Nonnull
	private BetreuungZeitabschnittDTO createInvalidZeitabschitt() {
		BetreuungZeitabschnittDTO invalid = new BetreuungZeitabschnittDTO();
		invalid.setAnzahlMonatlicheHauptmahlzeiten(-1);

		return invalid;
	}
}
