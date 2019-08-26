package ch.dvbern.kibon.api.verfuegung;

import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.TestcontainersEnvironment;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.jackson.JsonMatchers.isJsonStringMatching;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonArray;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

@QuarkusTestResource(TestcontainersEnvironment.class)
@QuarkusTest
class VerfuegungenResourceImplTest {

	@Test
	public void
	testGetAllEndpoint() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("verfuegungen", is(jsonArray()))
				.where("institutionen", is(jsonArray()))
			));
	}

	@Test
	public void
	testGetAllEndpointWithAfterIdParam() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen?after_id=10")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("verfuegungen", is(jsonArray()))
				.where("institutionen", is(jsonArray()))
			));
	}

	@Test
	public void
	testGetAllEndpointWithLimit() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen?limit=1")
			.then()
			.assertThat()
			.statusCode(Status.OK.getStatusCode())
			.body(isJsonStringMatching(jsonObject()
				.where("verfuegungen", is(jsonArray(hasSize(lessThanOrEqualTo(1)))))
				.where("institutionen", is(jsonArray(hasSize(lessThanOrEqualTo(1)))))
			));
	}

	@Test
	public void
	testGetAllEndpointLimitMustBeNonnegative() {
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("v1/verfuegungen?limit=-1")
			.then()
			.assertThat()
			.statusCode(Status.BAD_REQUEST.getStatusCode());
	}
}
