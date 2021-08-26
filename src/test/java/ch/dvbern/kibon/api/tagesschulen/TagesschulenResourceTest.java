package ch.dvbern.kibon.api.tagesschulen;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungDTO;
import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungZeitabschnittDTO;
import ch.dvbern.kibon.exchange.api.common.shared.Zeiteinheit;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.AbholungTagesschule;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.Intervall;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.ModulAuswahlDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleBestaetigungDTO;
import ch.dvbern.kibon.testutils.TestcontainersEnvironment;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.jackson.JsonMatchers.isJsonStringMatching;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonArray;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@QuarkusTestResource(TestcontainersEnvironment.class)
@QuarkusTest
public class TagesschulenResourceTest {

	private static final String REFNR = "20.000101.001.1.1";

	@Test
	void testGetAllEndpoint() {
		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.when()
			.get("/tagesschulen/anmeldungen")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("anmeldungen", is(jsonArray(is(not(empty())))))
			));
	}

	@Test
	void testPostConfirmRequiresAuthorisation() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.post("/tagesschulen/anmeldungen/refnr/" + REFNR)
			.then()
			.assertThat()
			.statusCode(Status.UNAUTHORIZED.getStatusCode());
	}

	@Test
	void testPostConfirmAnmeldungNotFound() {
		TagesschuleBestaetigungDTO invalid = create(createValidModul());

		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.body(invalid)
			.when()
			.post("/tagesschulen/anmeldungen/refnr/20.000102.001.1.1")
			.then()
			.assertThat()
			.statusCode(Status.NOT_FOUND.getStatusCode());
	}

	@Test
	void testPostConfirmRequiresValidDTO() {
		TagesschuleBestaetigungDTO invalid = create(createInvalidModul());

		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.body(invalid)
			.when()
			.post("/tagesschulen/anmeldungen/refnr/" + REFNR)
			.then()
			.assertThat()
			.statusCode(Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	void testPostConfirmAcceptsValidDTO() {
		TagesschuleBestaetigungDTO valid = create(createValidModul());

		given()
			.auth().oauth2(TestcontainersEnvironment.getAccessToken())
			.contentType(ContentType.JSON)
			.body(valid)
			.when()
			.post("/tagesschulen/anmeldungen/refnr/" + REFNR)
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode());
	}

	@Nonnull
	private TagesschuleBestaetigungDTO create(@Nonnull ModulAuswahlDTO modulAuswahlDTO) {
		TagesschuleBestaetigungDTO dto = new TagesschuleBestaetigungDTO();
		dto.setAbholung(AbholungTagesschule.ABHOLUNG);
		dto.setRefnr(REFNR);
		dto.setBemerkung("");
		dto.setAbweichungZweitesSemester(false);
		dto.setEintrittsdatum(LocalDate.now());
		dto.getModule().add(modulAuswahlDTO);

		return dto;
	}

	@Nonnull
	private ModulAuswahlDTO createValidModul() {
		ModulAuswahlDTO valid = new ModulAuswahlDTO();
		valid.setModulId("1");
		valid.setIntervall(Intervall.WOECHENTLICH);
		valid.setWochentag(DayOfWeek.MONDAY);

		return valid;
	}

	@Nonnull
	private ModulAuswahlDTO createInvalidModul() {
		ModulAuswahlDTO invalid = new ModulAuswahlDTO();
		invalid.setIntervall(Intervall.WOECHENTLICH);
		invalid.setWochentag(DayOfWeek.MONDAY);

		return invalid;
	}
}
