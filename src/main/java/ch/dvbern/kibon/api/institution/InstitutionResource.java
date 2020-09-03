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
import ch.dvbern.kibon.exchange.api.common.institution.InstitutionDTO;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.service.InstitutionService;
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

@Path("/institutions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InstitutionResource {
	private static final Logger LOG = LoggerFactory.getLogger(InstitutionResource.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	InstitutionService institutionService;

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
	@Operation(
		summary = "Institutions for family portal Bern",
		description = "Returns a list of institutions with additional data as required for the family portal Bern."
	)
	@SecurityRequirement(name = "OAuth2", scopes = "familyportal")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@APIResponse(responseCode = "504", ref = "#/components/responses/GatewayTimeOut")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("familyportal")
	@Timed(name = "stubTimer",
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
	@Operation(
		summary = "Returns institution for the give id.",
		description = "Returns institution for the give id to the client application.")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("user")
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load an Institution",
		unit = MetricUnits.MILLISECONDS)
	public Response get(
		@Parameter(description = "Institutions are identified with their IDs. Use this "
			+ "parameter to get the Institution with given id.")
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

		Client client = institutionService.getClient(id, clientName);

		if (client == null) {
			// Institution not found for given client
			return Response.status(Status.NOT_FOUND).build();
		}

		if (!client.getActive()) {
			// Client not active (forbidden) for given institution
			return Response.status(Status.FORBIDDEN).build();
		}

		// Get InstitutionDTO
		InstitutionDTO institutionDTO = institutionService.get(id);

		return Response.ok(institutionDTO).build();
	}
}
