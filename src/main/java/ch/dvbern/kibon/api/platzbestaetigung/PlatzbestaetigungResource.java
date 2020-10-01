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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.api.common.platzbestaetigung.BetreuungAnfrageDTO;
import ch.dvbern.kibon.exchange.api.common.platzbestaetigung.BetreuungAnfragenDTO;
import ch.dvbern.kibon.exchange.api.common.platzbestaetigung.BetreuungDTO;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungEventDTO;
import ch.dvbern.kibon.platzbestaetigung.facade.PlatzbestaetigungKafkaEventProducer;
import ch.dvbern.kibon.platzbestaetigung.model.ClientBetreuungAnfrageDTO;
import ch.dvbern.kibon.platzbestaetigung.service.BetreuungAnfrageService;
import ch.dvbern.kibon.platzbestaetigung.service.filter.ClientBetreuungAnfrageFilter;
import ch.dvbern.kibon.util.OpenApiTag;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/platzbestaetigung")
@Tag(name = OpenApiTag.PLATZ_BESTAETIGUNG)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlatzbestaetigungResource {

	private static final Logger LOG = LoggerFactory.getLogger(PlatzbestaetigungResource.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	BetreuungAnfrageService betreuungAnfrageService;

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
	PlatzbestaetigungKafkaEventProducer platzbestaetigungProducer;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ClientService clientService;

	@GET
	@Operation(
		summary = "Returniert Betreuung-Anfragen",
		description = "Wenn ein Betreuungs-Gesuch bei einer Institution in kiBon eingereicht wird, muss diese den "
			+ "Betreuungs-Platz des Kindes bestätigen.\n\nDiese Schnittstelle kann genutzt werden um alle "
			+ "Betreuungs-Anfragen zu laden, welche die Institutionen des Clients betreffen.")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("user")
	@Timed(name = "anfrageTimer",
		description = "A measure of how long it takes to load BetreuungAnfrage",
		unit = MetricUnits.MILLISECONDS)
	public BetreuungAnfragenDTO getAll(
		@Parameter(description = "Erlaubt es, nur neue BetreuungAnfragen zu laden.\n\nJede BetreuungAnfragen hat eine "
			+ "monoton steigende ID. Ein Client kann deshalb die grösste ID bereits eingelesener BetreuungAnfragen als"
			+ " `after_id` Parameter setzen, um nur die neu verfügbaren BetreuungAnfragen zu erhalten.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit,
		@Parameter(description = "Erweiterung für zusätzliche Filter - wird momentan nicht verwendet")
		@QueryParam("$filter") @Nullable String filter) {

		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"BetreuungAnfragen accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		ClientBetreuungAnfrageFilter queryFilter = new ClientBetreuungAnfrageFilter(clientName, afterId, limit);

		List<ClientBetreuungAnfrageDTO> dtos = betreuungAnfrageService.getAllForClient(queryFilter);

		List<BetreuungAnfrageDTO> betreuungAnfrageDTOs = dtos.stream()
			.map(this::convert)
			.collect(Collectors.toList());

		BetreuungAnfragenDTO result = new BetreuungAnfragenDTO();
		result.setAnfragen(betreuungAnfrageDTOs);

		return result;
	}

	@Nonnull
	private BetreuungAnfrageDTO convert(@Nonnull ClientBetreuungAnfrageDTO model) {
		return objectMapper.convertValue(model, BetreuungAnfrageDTO.class);
	}

	@POST
	@Operation(summary = "Eine Betreuung-Anfrage in kiBon bestätigen",
		description = "Diese Schnittstelle ermöglicht eine automatisierte Bestätigung einer Betreuung-Anfrage.")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Path("/betreuung")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("user")
	@Timed(name = "betreuungTimer",
		description = "A measure of how long it takes to process BetreuungDTO",
		unit = MetricUnits.MILLISECONDS)
	public Uni<Response> sendPlatzbestaetigungBetreuungToKafka(@Nonnull @NotNull @Valid BetreuungDTO betreuungDTO) {
		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Betreuung received by '{}' with clientName '{}', roles '{}'",
			userName,
			clientName,
			groups);

		BetreuungEventDTO betreuungEventDTO = objectMapper.convertValue(betreuungDTO, BetreuungEventDTO.class);

		String institutionId = betreuungEventDTO.getInstitutionId();
		Optional<Client> client = clientService.findActive(new ClientId(clientName, institutionId));

		if (client.isEmpty()) {
			return Uni.createFrom().item(Response.status(Status.FORBIDDEN).build());
		}

		LOG.debug("generating message");
		CompletionStage<Response> acked = platzbestaetigungProducer.process(betreuungEventDTO)
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
