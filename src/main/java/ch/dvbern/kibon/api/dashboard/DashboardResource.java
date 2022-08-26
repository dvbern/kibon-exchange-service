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

package ch.dvbern.kibon.api.dashboard;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ch.dvbern.kibon.exchange.api.common.dashboard.gemeinde.GemeindeDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.gemeinde.GemeindenDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.gemeindekennzahlen.GemeindeKennzahlenDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.gemeindekennzahlen.GemeindenKennzahlenDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.institution.InstitutionDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.institution.InstitutionenDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.lastenausgleich.LastenausgleicheDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.verfuegung.VerfuegungenDTO;
import ch.dvbern.kibon.gemeinde.service.GemeindeService;
import ch.dvbern.kibon.gemeindekennzahlen.model.GemeindeKennzahlen;
import ch.dvbern.kibon.gemeindekennzahlen.service.GemeindeKennzahlenService;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.util.OpenApiTag;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/dashboard")
@Tag(name = OpenApiTag.DASHBOARD)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardResource {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardResource.class);

	private static final String CLIENT_ID = "clientId";

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	GemeindeService gemeindeService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	GemeindeKennzahlenService gemeindeKennzahlenService;

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

	@GET
	@Path("/gemeinden")
	@Operation(
		summary = "Returniert alle Gemeinde.",
		description =
			"Ihr koennt alle Gemeinde Daten abholen, entweder von Anfang vor oder ab einen gewissen ID")
	@SecurityRequirement(name = "OAuth2", scopes = "dashboard")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("dashboard")
	@Valid
	public GemeindenDTO getAllGemeinden(
		@Parameter(description = "Erlaubt es, nur die Gemeinde zu laden, nach einem gewissen ID.\\n\\nJede "
			+ "Gemeinde hat eine monoton steigende ID.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Gemeinde Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		List<GemeindeDTO> gemeindeDTOs = gemeindeService.getAll(afterId, limit);

		GemeindenDTO result = new GemeindenDTO();
		result.setGemeinden(gemeindeDTOs);

		return result;
	}

	@GET
	@Path("/gemeinden-kennzahlen")
	@Operation(
		summary = "Returniert alle GemeindeKennzahlen.",
		description =
			"Ihr koennt alle GemeindeKennzahlen Daten abholen, entweder von Anfang vor oder ab einen gewissen ID")
	@SecurityRequirement(name = "OAuth2", scopes = "dashboard")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("dashboard")
	@Valid
	public GemeindenKennzahlenDTO getAllGemeindeKennzahlen(
		@Parameter(description = "Erlaubt es, nur die GemeindeKennzahlen zu laden, nach einem gewissen ID.\\n\\nJede "
			+ "GemeindeKennzahlen hat eine monoton steigende ID.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Gemeindekennzahlen Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		List<GemeindeKennzahlen> gemeindeKennzahlen = gemeindeKennzahlenService.getAll(afterId, limit);
		List<GemeindeKennzahlenDTO> gemeindeKennzahlenDTOs = gemeindeKennzahlen.stream()
			.map(this::convertGemeindeKennzahlen)
			.collect(Collectors.toList());
		GemeindenKennzahlenDTO result = new GemeindenKennzahlenDTO();
		result.setGemeindenKennzahlen(gemeindeKennzahlenDTOs);

		return result;
	}

	@GET
	@Path("/institutionen")
	@Operation(
		summary = "Returniert alle Institutionen.",
		description =
			"Ihr koennt alle Institutionen Daten abholen, entweder von Anfang vor oder ab einen gewissen ID")
	@SecurityRequirement(name = "OAuth2", scopes = "dashboard")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("dashboard")
	@Valid
	public InstitutionenDTO getAllInstitutionen(
		@Parameter(description = "Erlaubt es, nur die Instutionen zu laden, nach einem gewissen ID.\\n\\nJede "
			+ "Institution hat eine monoton steigende ID.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Institution Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		List<Institution> institutionen = institutionService.getAllForDashboard(afterId, limit);

		List<InstitutionDTO> institutionDTOS = institutionen.stream()
			.map(this::convertInstitution)
			.collect(Collectors.toList());

		InstitutionenDTO result = new InstitutionenDTO();
		result.setInstitutionen(institutionDTOS);

		return result;
	}

	@GET
	@Path("/lastenausgleiche")
	@Operation(
		summary = "Returniert alle Lastenausgleich.",
		description =
			"Ihr koennt alle Lastenausgleich Daten abholen, entweder von Anfang vor oder ab einen gewissen ID")
	@SecurityRequirement(name = "OAuth2", scopes = "dashboard")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("dashboard")
	@Valid
	public LastenausgleicheDTO getAllLats(
		@Parameter(description = "Erlaubt es, nach diesem ID Lastenausgleichdaten zu laden, nach einem gewissen ID.\n\nJede Lastenausgleich hat eine "
			+ "monoton steigende ID.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Lastenausgleich Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);
		return new LastenausgleicheDTO();
	}

	@GET
	@Path("/verfuegungen")
	@Operation(
		summary = "Returniert alle Verfuegungen.",
		description =
			"Ihr koennt alle Verfuegungen Daten abholen, entweder von Anfang vor oder ab einen gewissen ID")
	@SecurityRequirement(name = "OAuth2", scopes = "dashboard")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("dashboard")
	@Valid
	public VerfuegungenDTO getAllVerfuegungen(
		@Parameter(description = "Erlaubt es, nach diesem ID Verfuegungen zu laden.\n\nJede Verfuegung hat eine "
			+ "monoton steigende ID.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Verfuegung Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);
		return new VerfuegungenDTO();
	}

	@Nonnull
	private GemeindeKennzahlenDTO convertGemeindeKennzahlen(@Nonnull GemeindeKennzahlen model) {
		return objectMapper.convertValue(model, GemeindeKennzahlenDTO.class);
	}

	@Nonnull
	private InstitutionDTO convertInstitution(@Nonnull Institution model) {
		InstitutionDTO institutionDTO = objectMapper.convertValue(model, InstitutionDTO.class);
		if (model.getKontaktAdresse().getGemeinde() != null) {
			institutionDTO.getAdresse()
				.setStandortGemeinde(model.getKontaktAdresse().getGemeinde().getName() != null ?
					model.getKontaktAdresse().getGemeinde().getName() :
					"");
			institutionDTO.getAdresse()
				.setStandortGemeindeBFSNummer(model.getKontaktAdresse().getGemeinde().getBfsNummer() != null ?
					model.getKontaktAdresse().getGemeinde().getBfsNummer().toString() :
					"");
		}
		return institutionDTO;
	}
}
