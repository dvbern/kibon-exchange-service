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

package ch.dvbern.kibon.api.betreuung;

import java.util.Set;

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

import ch.dvbern.kibon.betreuung.model.BetreuungStornierungDTO;
import ch.dvbern.kibon.betreuung.service.BetreuungStornierungAnfrageService;
import ch.dvbern.kibon.util.OpenApiTag;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/betreuung")
@Tag(name = OpenApiTag.BETREUUNGEN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BetreuungResource {

	private static final Logger LOG = LoggerFactory.getLogger(BetreuungResource.class);

	@SuppressWarnings({ "checkstyle:VisibilityModifier", "CdiInjectionPointsInspection" })
	@Inject
	JsonWebToken jsonWebToken;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	SecurityIdentity identity;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	BetreuungStornierungAnfrageService betreuungStornierungAnfrageService;


	@POST
	@Operation(summary = "Eine Betreuung in kiBon stornieren",
		description = "Diese Schnittstelle ermöglicht eine automatisierte Stornierung einer Betreuung in kiBon")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "201", content = {})
	@APIResponse(responseCode = "401", ref = "#components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Path("/stornieren")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("user")
	@Timed(name = "betreuungStornierenTimer",
		description = "A measure of how long it takes to process a Stornieranfrage",
		unit = MetricUnits.MILLISECONDS)
	public void sendBetreuungStornierungToKafka(@Nonnull @NotNull @Valid BetreuungStornierungDTO betreuungStornierungDTO) {
		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Stornierung received by '{}' with clientName '{}', roles '{}'",
			userName,
			clientName,
			groups);

	}
}
