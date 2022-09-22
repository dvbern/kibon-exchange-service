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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
import ch.dvbern.kibon.exchange.api.common.dashboard.institution.AdresseInstitutionDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.institution.InstitutionDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.institution.InstitutionenDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.lastenausgleich.LastenausgleicheDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.verfuegung.VerfuegungDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.verfuegung.VerfuegungenDTO;
import ch.dvbern.kibon.exchange.commons.types.Mandant;
import ch.dvbern.kibon.gemeinde.service.GemeindeService;
import ch.dvbern.kibon.gemeindekennzahlen.model.GemeindeKennzahlen;
import ch.dvbern.kibon.gemeindekennzahlen.service.GemeindeKennzahlenService;
import ch.dvbern.kibon.institution.model.Gemeinde;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.util.OpenApiTag;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

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

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	VerfuegungService verfuegungService;

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
	@Operation(summary = "Returniert alle Gemeinden.")
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
		@Parameter(description = "Erlaubt es, nur Gemeinden zu laden, mit einer grösseren sequenceId.\n\nJede "
			+ "Gemeinde hat eine monoton steigende sequenceId.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		Mandant mandant = Mandant.BERN;

		LOG.info(
			"Gemeinde Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id "
				+ "'{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		List<GemeindeDTO> gemeindeDTOs = gemeindeService.getAll(afterId, limit, mandant);

		GemeindenDTO result = new GemeindenDTO();
		result.setGemeinden(gemeindeDTOs);

		return result;
	}

	@GET
	@Path("/gemeinden-kennzahlen")
	@Operation(summary = "Returniert alle GemeindeKennzahlen.")
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
		@Parameter(description = "Erlaubt es, nur GemeindeKennzahlen zu laden, mit einer grösseren sequenceId.\n\nJede"
			+ " GemeindeKennzahlen hat eine monoton steigende sequenceId.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Gemeindekennzahlen Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and "
				+ "after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		Mandant mandant = Mandant.BERN;

		List<GemeindeKennzahlen> gemeindeKennzahlen = gemeindeKennzahlenService.getAll(afterId, limit, mandant);
		List<GemeindeKennzahlenDTO> gemeindeKennzahlenDTOs = gemeindeKennzahlen.stream()
			.map(this::convertGemeindeKennzahlen)
			.collect(Collectors.toList());
		GemeindenKennzahlenDTO result = new GemeindenKennzahlenDTO();
		result.setGemeindenKennzahlen(gemeindeKennzahlenDTOs);

		return result;
	}

	@GET
	@Path("/institutionen")
	@Operation(summary = "Returniert alle Institutionen.")
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
		@Parameter(description = "Erlaubt es, nur Instutionen zu laden, mit einer grösseren sequenceId.\n\nJede "
			+ "Institution hat eine monoton steigende sequenceId.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		Mandant mandant = Mandant.BERN;

		LOG.info(
			"Institution Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id"
				+ " '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		List<Institution> institutionen = institutionService.getAllForDashboard(afterId, limit, mandant);

		List<InstitutionDTO> institutionDTOS = institutionen.stream()
			.map(this::convertInstitution)
			.collect(Collectors.toList());

		InstitutionenDTO result = new InstitutionenDTO();
		result.setInstitutionen(institutionDTOS);

		return result;
	}

	@GET
	@Path("/lastenausgleiche")
	@Operation(summary = "Returniert alle Lastenausgleiche.")
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
		@Parameter(description =
			"Erlaubt es, nur Lastenausgleichdaten zu laden, mit einer grösseren sequenceId.\n\nJede "
				+ "Lastenausgleiche hat eine monoton steigende sequenceId.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Lastenausgleich Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and "
				+ "after_id '{}'",
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

		Mandant mandant = Mandant.BERN;

		LOG.info(
			"Verfuegung Dashboard Resource accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id "
				+ "'{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);
		List<Verfuegung> verfuegungen = verfuegungService.getAllForDashboard(afterId, limit, mandant);

		List<VerfuegungDTO> verfuegungDTOS = verfuegungen.stream()
			.map(this::convertVerfuegung)
			.collect(Collectors.toList());

		VerfuegungenDTO result = new VerfuegungenDTO();
		result.setVerfuegungen(verfuegungDTOS);

		return result;
	}

	@Nonnull
	private GemeindeKennzahlenDTO convertGemeindeKennzahlen(@Nonnull GemeindeKennzahlen model) {
		return objectMapper.convertValue(model, GemeindeKennzahlenDTO.class);
	}

	@Nonnull
	private InstitutionDTO convertInstitution(@Nonnull Institution model) {
		InstitutionDTO institutionDTO = objectMapper.convertValue(model, InstitutionDTO.class);
		AdresseInstitutionDTO adresse = institutionDTO.getAdresse();
		Gemeinde gemeinde = model.getKontaktAdresse().getGemeinde();

		if (gemeinde != null) {
			adresse.setStandortGemeinde(gemeinde.getName() != null ? gemeinde.getName() : "");
			String bfsNummer = gemeinde.getBfsNummer() != null ? String.valueOf(gemeinde.getBfsNummer()) : "";
			adresse.setStandortGemeindeBFSNummer(bfsNummer);
		}

		return institutionDTO;
	}

	@Nonnull
	private VerfuegungDTO convertVerfuegung(@Nonnull Verfuegung model) {
		VerfuegungDTO verfuegungDTO = objectMapper.convertValue(model, VerfuegungDTO.class);
		verfuegungDTO.getZeitabschnitte().forEach(zeitabschnittDTO -> {
			BigDecimal gutschein = requireNonNull(zeitabschnittDTO.getBetreuungsgutschein());
			BigDecimal gutscheinGemeinde = gutschein.subtract(zeitabschnittDTO.getBetreuungsgutscheinKanton())
				.setScale(2, RoundingMode.HALF_UP);
			zeitabschnittDTO.setBetreuungsgutscheinGemeinde(gutscheinGemeinde);
		});

		verfuegungDTO.getKind().setKindHash(hashKind(requireNonNull(model.getKind())));

		// anonymisieren das Geburtsdatum bis man sicher sind ob es kann exportiert werden
		LocalDate geburtsdatumAnonymisiert = verfuegungDTO.getKind().getGeburtsdatum().withDayOfMonth(1).withMonth(1);
		verfuegungDTO.getKind().setGeburtsdatum(geburtsdatumAnonymisiert);

		return verfuegungDTO;
	}

	@Nonnull
	private String hashKind(@Nonnull JsonNode kind) {
		return String.valueOf(
			Objects.hash(
				kind.get("vorname").asText(),
				kind.get("nachname").asText(),
				kind.get("geburtsdatum").asText()
			));
	}
}
