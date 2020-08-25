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
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.api.common.platzbestaetigung.BetreuungAnfrageDTO;
import ch.dvbern.kibon.exchange.api.common.platzbestaetigung.BetreuungDTO;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungEventDTO;
import ch.dvbern.kibon.platzbestaetigung.facade.BetreuungKafkaEventProducer;
import ch.dvbern.kibon.platzbestaetigung.model.ClientBetreuungAnfrageDTO;
import ch.dvbern.kibon.platzbestaetigung.service.BetreuungAnfrageService;
import ch.dvbern.kibon.platzbestaetigung.service.filter.ClientBetreuungAnfrageFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/platzbestaetigung")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlatzbestaetigungResource {

	private static final Logger LOG = LoggerFactory.getLogger(PlatzbestaetigungResource.class);

	//private static final int UNAUTHORIZED = 401;

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
	BetreuungKafkaEventProducer betreuungProducer;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ClientService clientService;

	@GET
	@Operation(
		summary = "Returns all kiBon BetreuungAnfrage which were made available.",
		description = "Returns all kiBon BetreuungAnfrage, which were made available "
			+ "to the client in the kiBon application.")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200", name = "List<BetreuungAnfrageDTO>")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("user")
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load BetreuungAnfrage",
		unit = MetricUnits.MILLISECONDS)
	public List<BetreuungAnfrageDTO> getAll(
		@Parameter(description = "BetreuungAnfragen are ordered by their strictly monotonically increasing ID. Use "
			+ "this "
			+ "parameter to get only BetreuungAnfragen with ID larger after_id. Useful to exclude already fetched "
			+ "BetreuungAnfragen.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Limits the maximum result set of Verfuegungen to the specified number")
		@Min(0) @QueryParam("limit") @Nullable Integer limit,
		@Parameter(description = "Extension point for additional filtering, e.g. by institution. Currently not used.")
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
		return betreuungAnfrageDTOs;
	}

	@Nonnull
	private BetreuungAnfrageDTO convert(@Nonnull ClientBetreuungAnfrageDTO model) {
		return objectMapper.convertValue(model, BetreuungAnfrageDTO.class);
	}

	@PUT
	@Operation(summary = "Put a Betreuung into Kafka for Kibon.",
		description = "This service allow to put a BetreuungDTO into the kafka Topic for kiBon")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200", name = "Accepted")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@Path("/betreuung")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("user")
	public Response sendBetreuungToKafka(
		@Nonnull @NotNull BetreuungDTO betreuungDTO
	){
		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Betreuung sended by '{}' with clientName '{}', roles '{}'",
			userName,
			clientName,
			groups);

		BetreuungEventDTO betreuungEventDTO = objectMapper.convertValue(betreuungDTO,
			BetreuungEventDTO.class);

		//Todo temporary deactivated for testing Kafka need to be activated again
		/*Optional<Client> client = clientService.find(new ClientId(clientName,
			betreuungEventDTO.getInstitutionId()));

		if(!client.isPresent() || !client.get().getActive()){
			return Response.status(UNAUTHORIZED).build();
		}
*/
		//send Event an kafka
		betreuungProducer.process(betreuungEventDTO);
		return Response.ok().build();
	}
}
