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

import ch.dvbern.kibon.api.betreuung.BetreuungResource;
import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungAnfragenDTO;
import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungDTO;
import ch.dvbern.kibon.util.OpenApiTag;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;

@Path("/platzbestaetigung")
@Tag(name = OpenApiTag.BETREUUNGEN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlatzbestaetigungResource {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	BetreuungResource betreuungResource;

	@GET
	@Operation(deprecated = true, summary = "Siehe /api/v1/betreuung")
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
		@Parameter(description = "Erlaubt es, nur neue BetreuungAnfragen zu laden.\n\n"
			+ "Jede BetreuungAnfragen hat eine monoton steigende ID. Ein Client kann deshalb die grösste ID bereits "
			+ "eingelesener BetreuungAnfragen als `after_id` Parameter setzen, um nur die neu verfügbaren "
			+ "BetreuungAnfragen zu erhalten.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit,
		@Parameter(description = "Erweiterung für zusätzliche Filter - wird momentan nicht verwendet")
		@QueryParam("$filter") @Nullable String filter) {

		return betreuungResource.getAll(afterId, limit, filter);
	}

	@POST
	@Operation(deprecated = true, summary = "Siehe /api/v1/betreuung")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200", content = {})
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

		return betreuungResource.sendBetreuungToKafka(betreuungDTO);
	}
}
