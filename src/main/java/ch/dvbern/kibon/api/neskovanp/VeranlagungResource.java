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

package ch.dvbern.kibon.api.neskovanp;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.kibon.exchange.api.common.neskovanp.NeueVeranlagungDTO;
import ch.dvbern.kibon.exchange.commons.neskovanp.NeueVeranlagungEventDTO;
import ch.dvbern.kibon.neskovanp.facade.NeueVeranlagungKafkaEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/neskovanp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VeranlagungResource {
	private static final Logger LOG = LoggerFactory.getLogger(VeranlagungResource.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper objectMapper;

	@SuppressWarnings({ "checkstyle:VisibilityModifier", "CdiInjectionPointsInspection" })
	@Inject
	JsonWebToken jsonWebToken;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	SecurityIdentity identity;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	NeueVeranlagungKafkaEventProducer veranlagungProducer;

	@POST
	@Operation(summary = "Eine neue Veranlagung an kiBon melden",
		description = "Diese Schnittstelle meldet kiBon dass eine neue Steuer-Veranlagung verfügbar ist.")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200", content = {})
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Path("/veranlagung")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("neskovanp")
	@Timed(name = "veranlagungTimer",
		description = "A measure of how long it takes to process VeranlagungDTO",
		unit = MetricUnits.MILLISECONDS)
	public Uni<Response> sendNeueVeranlagungToKafka(@Nonnull @NotNull @Valid NeueVeranlagungDTO veranlagungDTO) {

		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Neue Veranlagung Event received by '{}' with clientName '{}', roles '{}'",
			userName,
			clientName,
			groups);

		NeueVeranlagungEventDTO neueVeranlagungEventDTO = objectMapper.convertValue(veranlagungDTO, NeueVeranlagungEventDTO.class);

		LOG.debug("generating message");
		CompletionStage<Response> acked = veranlagungProducer.process(neueVeranlagungEventDTO, clientName)
			.thenApply(Void -> {
				LOG.debug("received ack");
				return Response.ok().build();
			})
			.exceptionally(error -> {
				LOG.error("failed", error);
				return Response.serverError().build();
			});
		LOG.debug("received completion stage");

		return Uni.createFrom().completionStage(acked);
	}
}
