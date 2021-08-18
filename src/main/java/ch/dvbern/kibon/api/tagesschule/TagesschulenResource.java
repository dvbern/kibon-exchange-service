/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.api.tagesschule;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleAnmeldungenDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleBestaetigungDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.tarife.TagesschuleTarifeDTO;
import ch.dvbern.kibon.util.OpenApiTag;
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
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/tagesschulen")
@Tag(name = OpenApiTag.TAGES_SCHULEN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagesschulenResource {

	private static final Logger LOG = LoggerFactory.getLogger(TagesschulenResource.class);

	private static final String REF_NR_1 = "20.007420.001.1.3";
	private static final String REF_NR_2 = "20.007404.002.1.3";

	@SuppressWarnings({ "checkstyle:VisibilityModifier", "CdiInjectionPointsInspection" })
	@Inject
	JsonWebToken jsonWebToken;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	SecurityIdentity identity;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	TagesschulenMockResponses mockResponses;

	@GET
	@Path("/anmeldungen")
	@Operation(
		summary = "Returniert alle Anmeldungen bei Tagesschulen.",
		description =
			"In kiBon können Personen ihr Kind bei einer Tagesschule anmelden. Sie wählen dabei die "
				+ "Betreuungsinstitution und die gewünschten Betreuungs-Module aus.\n"
				+ "Wenn die Anmeldung freigegeben wird, kann über diese Schnittstelle die Anmeldung abgefragt werden."
				+ "\n\n"
				+ "Ein/e Sachbearbeiter/in der Tagesschule muss die Modul-Auswahl bestätigen. Dies kann entweder "
				+ "direkt in kiBon erfolgen, oder durch die Bestätigungs-Schnittstelle."
				+ "\n\n"
				+ "Nach der elektronischen Anmeldung muss von der antragstellenden Person eine schriftliche "
				+ "Freigabequittung unterschrieben an die zuständige Geminde gesendet werden.\n"
				+ "Wenn die Quittung durch die Gemeinde eingelesen wurde, wird - auch durch die Gemeinde - die "
				+ "finanzielle Situation der antragstellenden Person geprüft. Erst wenn auch dieser Prozess "
				+ "abgeschlossen wird, liegt der rechtlich geltende Tarif für die Betreuung und Verpflegung vor."
				+ "\n\n"
				+ "Es werden alle vorhandenen Anmeldungen zurückgegeben, für welche der aufrufende Client in kiBon "
				+ "eine Berechtigung erhalten hat."
				+ "\n\n"
				+ "Im Resultat befinden sich die Anmeldungen selbst, sowie zusätzliche Informationen zu den "
				+ "betroffenen Betreuungs-Modulen.")
	@SecurityRequirement(name = "OAuth2", scopes = "tagesschule")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("tagesschule")
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load Anmeldungen",
		unit = MetricUnits.MILLISECONDS)
	@Valid
	public TagesschuleAnmeldungenDTO getAll(
		@Parameter(description = "Erlaubt es, nur neue Anmeldung zu laden.\n\nJede Anmeldung hat eine "
			+ "monoton steigende ID. Ein Client kann deshalb die grösste ID bereits eingelesener Anmeldungen als"
			+ " `after_id` Parameter setzen, um nur die neu verfügbaren Anmeldung zu erhalten.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
		@Min(0) @QueryParam("limit") @Nullable Integer limit,
		@Parameter(description = "Erweiterung für zusätzliche Filter - wird momentan nicht verwendet")
		@QueryParam("$filter") @Nullable String filter) {

		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Tagesschule-Anmeldungen accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		TagesschuleAnmeldungenDTO anmeldungenDTO = new TagesschuleAnmeldungenDTO();

		Long maxSize = Optional.ofNullable(limit)
			.map(Long::valueOf)
			.orElse(Long.MAX_VALUE);

		Stream.of(mockResponses.createAnmeldung1(REF_NR_1), mockResponses.createAnmeldung2(REF_NR_2))
			.filter(a -> afterId == null || a.getId() > afterId)
			.limit(maxSize)
			.forEach(a -> anmeldungenDTO.getAnmeldungen().add(a));

		return anmeldungenDTO;
	}

	@DELETE
	@Path("/anmeldungen/refnr/{refnr}")
	@Operation(
		summary = "Ablehnen einer Anmeldung.",
		description = "Über diese Schnittstelle kann eine Anmeldung durch eine Tagesschule abgelehnt werden.")
	@SecurityRequirement(name = "OAuth2", scopes = "tagesschule")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("tagesschule")
	public Response reject(@NotEmpty @PathParam("refnr") String refnr) {

		return mockResponse(refnr);
	}

	@POST
	@Path("/anmeldungen/refnr/{refnr}")
	@Operation(
		summary = "Bestätigen einer Anmeldung.",
		description = "Bestätigt die Anmeldung bei einer Tagesschule, insbesondere der ausgewählten "
			+ "Betreuungs-Modulen."
			+ "\n\n"
			+ "Zwingend sind nur die Felder `refnr` und `module`.\n"
			+ "Falls ein anderes Feld gesetzt wird, überschreibt dies die entsprechenden Werte in kiBon, wird `null` "
			+ "übergeben, bleiben die kiBon Werte bestehen.")
	@SecurityRequirement(name = "OAuth2", scopes = "tagesschule")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("tagesschule")
	public Response confirm(
		@NotEmpty @PathParam("refnr") String refnr,
		@NotNull @Valid TagesschuleBestaetigungDTO bestaetigungDTO) {

		return mockResponse(refnr);
	}

	@GET
	@Path("/tarife/refnr/{refnr}")
	@Operation(
		summary = "Tarife einer Tagesschulen-Betreuung",
		description = "Gibt die Tarife einer bestätigten Tagesschulen-Betreuung zurück."
			+ "\n\n"
			+ "Die Tarife sind abhängig von der finanziellen Situation der Familie und müssen durch die Gemeinde "
			+ "verfügt werden."
			+ "\n\n"
			+ "Solange die Anmledung noch nicht vollständig geprüft wurde, wird der Maximaltarif zurückgegeben.")
	@SecurityRequirement(name = "OAuth2", scopes = "tagesschule")
	@APIResponse(responseCode = "200",
		content = @Content(schema = @Schema(implementation = TagesschuleTarifeDTO.class)))
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("tagesschule")
	public Response getTarife(@NotEmpty @PathParam("refnr") String refnr) {

		if (REF_NR_1.equals(refnr)) {
			return Response.ok(mockResponses.createTarif1(refnr)).build();
		}

		if (REF_NR_2.equals(refnr)) {
			return Response.ok(mockResponses.createTarif2(refnr)).build();
		}

		return Response.status(Status.NOT_FOUND).build();
	}

	@Nonnull
	private Response mockResponse(@Nonnull String refnr) {
		if (REF_NR_1.equals(refnr) || REF_NR_2.equals(refnr)) {
			return Response.ok().build();
		}

		return Response.status(Status.NOT_FOUND).build();
	}
}
