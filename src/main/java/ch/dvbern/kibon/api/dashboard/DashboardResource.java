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

import java.util.Set;

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
import ch.dvbern.kibon.exchange.api.common.dashboard.gemeindekennzahlen.GemeindeKennzahlenDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.institution.InstitutionDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.lastenausgleich.LastenausgleichDTO;
import ch.dvbern.kibon.exchange.api.common.dashboard.verfuegung.VerfuegungDTO;
import ch.dvbern.kibon.util.OpenApiTag;
import io.quarkus.security.identity.SecurityIdentity;
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

@Path("/dashboard")
@Tag(name = OpenApiTag.DASHBOARD)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardResource {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardResource.class);

	private static final String CLIENT_ID = "clientId";

	@SuppressWarnings({ "checkstyle:VisibilityModifier", "CdiInjectionPointsInspection" })
	@Inject
	JsonWebToken jsonWebToken;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	SecurityIdentity identity;

	@GET
	@Path("/gemeinde")
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
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load Gemeinden",
		unit = MetricUnits.MILLISECONDS)
	@Valid
	public GemeindeDTO getAllGemeinden(
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
		return new GemeindeDTO();
	}

	@GET
	@Path("/gemeindeKennzahlen")
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
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load GemeindeKennzahlen",
		unit = MetricUnits.MILLISECONDS)
	@Valid
	public GemeindeKennzahlenDTO getAllGemeindeKennzahlen(
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
		return new GemeindeKennzahlenDTO();
	}

	@GET
	@Path("/institution")
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
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load Institutionen",
		unit = MetricUnits.MILLISECONDS)
	@Valid
	public InstitutionDTO getAllInstitutionen(
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
		return new InstitutionDTO();
	}

	@GET
	@Path("/lastenausgleich")
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
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load Lastenausgleich Daten",
		unit = MetricUnits.MILLISECONDS)
	@Valid
	public LastenausgleichDTO getAllLats(
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
		return new LastenausgleichDTO();
	}

	@GET
	@Path("/verfuegung")
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
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load Verfuegungen",
		unit = MetricUnits.MILLISECONDS)
	@Valid
	public VerfuegungDTO getAllVerfuegungen(
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
		return new VerfuegungDTO();
	}
}
