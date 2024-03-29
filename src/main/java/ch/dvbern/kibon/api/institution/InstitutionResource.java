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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.api.institution.familyportal.FamilyPortalDTO;
import ch.dvbern.kibon.api.institution.familyportal.FamilyPortalInstitutionDTO;
import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.api.common.institution.ClientInstitutionDTO;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.util.OpenApiTag;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/institutions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InstitutionResource {
	private static final Logger LOG = LoggerFactory.getLogger(InstitutionResource.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	InstitutionService institutionService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ClientService clientService;

	@SuppressWarnings({ "checkstyle:VisibilityModifier", "CdiInjectionPointsInspection" })
	@Inject
	JsonWebToken jsonWebToken;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	SecurityIdentity identity;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper objectMapper;

	/**
	 * This endpoint is used by by company Internezzo to implement a new version of
	 * <a href="https://www.fambe.sites.be.ch/fambe_sites/de/index/kitas_tagesfamilienfinden/kitas_tagesfamilienfinden/kitas_tagesfamilienfinden.html">Familienportal</a>.
	 */
	@GET
	@Path("/familyportal")
	@Tag(name = OpenApiTag.FAMILIEN_PORTAL)
	@Operation(
		summary = "Institutionen für das Familienportal Bern",
		description = "Returniert eine Liste aller Kitas und Tagesfamilien Organisationen, welche in kiBon erfasst "
			+ "wurden."
	)
	@SecurityRequirement(name = "OAuth2", scopes = "familyportal")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("familyportal")
	@Timed(name = "familyportalTimer",
		description = "A measure of how long it takes to load FamilyPortalDTO",
		unit = MetricUnits.MILLISECONDS)
	public FamilyPortalDTO getForFamilyPortal() {
		List<Institution> all = institutionService.getForFamilyPortal();
		FamilyPortalDTO dto = new FamilyPortalDTO();

		dto.setInstitutionen(Arrays.asList(objectMapper.convertValue(all, FamilyPortalInstitutionDTO[].class)));
		return dto;
	}

	@GET
	@Path("{id}")
	@Tag(name = OpenApiTag.BETREUUNGEN)
	@Tag(name = OpenApiTag.TAGES_SCHULEN)
	@Operation(
		summary = "Institutions Daten",
		description = "Returniert Namen und Adresse einer Institution.")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200",
		content = @Content(schema = @Schema(implementation = ClientInstitutionDTO.class)))
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed({ "user", "tagesschule" })
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load an Institution",
		unit = MetricUnits.MILLISECONDS)
	public Response get(
		@Parameter(description = "ID der angeforderten Institution.")
		@PathParam("id") @Nonnull String id) {

		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Institutions accessed by '{}' with clientName '{}', roles '{}' and id '{}'",
			userName,
			clientName,
			groups,
			id);

		Response response = clientService.find(new ClientId(clientName, id))
			.map(this::toClientResponse)
			// Institution not found for given client
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());

		return response;
	}

	@Nonnull
	private Response toClientResponse(@Nonnull Client client) {
		if (!client.getActive()) {
			// Client not active (forbidden) for given institution
			return Response.status(Status.FORBIDDEN).build();
		}

		Optional<ClientInstitutionDTO> institutionDTO = institutionService.find(client);

		return institutionDTO.map(Response::ok)
			.orElseGet(() -> Response.status(Status.NOT_FOUND))
			.build();
	}
}
