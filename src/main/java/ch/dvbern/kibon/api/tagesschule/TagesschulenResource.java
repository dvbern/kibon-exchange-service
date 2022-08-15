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

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.api.shared.ClientInstitutionFilterParams;
import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.api.common.tagesschule.TagesschuleModuleDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleAnmeldungDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleAnmeldungenDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleBestaetigungDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.tarife.TagesschuleTarifeDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleBestaetigungEventDTO;
import ch.dvbern.kibon.shared.filter.FilterController;
import ch.dvbern.kibon.shared.filter.FilterControllerFactory;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity;
import ch.dvbern.kibon.tagesschulen.facade.AblehnenAnmeldungKafkaEventProducer;
import ch.dvbern.kibon.tagesschulen.facade.AnmeldungKafkaEventProducer;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldungDTO;
import ch.dvbern.kibon.tagesschulen.service.AnmeldungService;
import ch.dvbern.kibon.tagesschulen.service.TagesschuleModuleService;
import ch.dvbern.kibon.util.OpenApiTag;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
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

import static ch.dvbern.kibon.util.StringUtil.isBlankString;

@Path("/tagesschulen")
@Tag(name = OpenApiTag.TAGES_SCHULEN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagesschulenResource {

	private static final Logger LOG = LoggerFactory.getLogger(TagesschulenResource.class);

	private static final String CLIENT_ID = "clientId";

	@SuppressWarnings({ "checkstyle:VisibilityModifier", "CdiInjectionPointsInspection" })
	@Inject
	JsonWebToken jsonWebToken;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	SecurityIdentity identity;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	AnmeldungService anmeldungService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	TagesschuleModuleService tagesschuleModuleService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ClientService clientService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	AnmeldungKafkaEventProducer anmeldungKafkaEventProducer;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	AblehnenAnmeldungKafkaEventProducer ablehnenAnmeldungKafkaEventProducer;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper objectMapper;

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
	public TagesschuleAnmeldungenDTO getAll(@Valid @BeanParam ClientInstitutionFilterParams filterParams) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Tagesschule-Anmeldungen accessed by '{}' with clientName '{}', roles '{}' and filter '{}'",
			userName,
			clientName,
			groups,
			filterParams);

		FilterController<ClientAnmeldung, ClientAnmeldungDTO> queryFilter =
			FilterControllerFactory.anmeldungenFilter(clientName, filterParams);

		List<ClientAnmeldungDTO> clientAnmeldungen = anmeldungService.getAllForClient(queryFilter);

		List<TagesschuleAnmeldungDTO> tagesschuleAnmeldungDTOS = clientAnmeldungen.stream()
			.map(this::convert)
			.collect(Collectors.toList());

		TagesschuleAnmeldungenDTO anmeldungenDTO = new TagesschuleAnmeldungenDTO();
		anmeldungenDTO.setAnmeldungen(tagesschuleAnmeldungDTOS);

		return anmeldungenDTO;
	}

	@Nonnull
	private TagesschuleAnmeldungDTO convert(@Nonnull ClientAnmeldungDTO model) {
		return objectMapper.convertValue(model, TagesschuleAnmeldungDTO.class);
	}

	@GET
	@Path("/anmeldungen/refnr/{refnr}")
	@Operation(summary = "Returniert die aktuellest Anmeldung zu der Referenznummer")
	@SecurityRequirement(name = "OAuth2", scopes = "tagesschule")
	@APIResponse(responseCode = "200",
		content = @Content(schema = @Schema(implementation = TagesschuleAnmeldungDTO.class)))
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("tagesschule")
	public Response getLatestAnmeldung(@NotEmpty @PathParam("refnr") String refnr) {
		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Tagesschule-Anmeldung accessed by '{}' with clientName '{}', roles '{}'. refNr '{}'",
			userName,
			clientName,
			groups,
			refnr);

		Optional<ClientAnmeldungDTO> anmeldung = anmeldungService.getLatestClientAnmeldung(clientName, refnr);

		if (anmeldung.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}

		TagesschuleAnmeldungDTO tagesschuleAnmeldungDTOS = convert(anmeldung.get());

		return Response.ok(tagesschuleAnmeldungDTOS).build();
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
	public Uni<Response> reject(@NotEmpty @PathParam("refnr") String refnr) {
		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Tagesschule Ablehnung received by '{}' with clientName '{}', roles '{}', refNr '{}'",
			userName,
			clientName,
			groups,
			refnr);

		//Find institution linked with refnummer
		Optional<Anmeldung> anmeldung = anmeldungService.getLatestAnmeldung(refnr);

		if (anmeldung.isEmpty()) {
			return Uni.createFrom().item(Response.status(Status.NOT_FOUND).build());
		}

		ClientId clientId = new ClientId(clientName, anmeldung.get().getInstitutionId());
		Optional<Client> client = clientService.findActive(clientId);

		if (client.isEmpty()) {
			return Uni.createFrom().item(Response.status(Status.FORBIDDEN).build());
		}

		LOG.debug("Generating message");

		CompletionStage<Response> acked =
			ablehnenAnmeldungKafkaEventProducer.process(refnr, client.get())
				.thenApply(Void -> {
					LOG.debug("received ack");
					return Response.ok().build();
				})
				.exceptionally(error -> {
					LOG.error("failed", error);
					return Response.serverError().build();
				});

		return Uni.createFrom().completionStage(acked);
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
	public Uni<Response> confirm(
		@NotEmpty @PathParam("refnr") String refnr,
		@NotNull @Valid TagesschuleBestaetigungDTO bestaetigungDTO) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"TagesschuleBestaetigung received by '{}' with clientName '{}', roles '{}', refNr '{}'",
			userName,
			clientName,
			groups,
			refnr);

		for (var modulAuswahlDTO : bestaetigungDTO.getModule()) {
			if (isBlankString(modulAuswahlDTO.getModulId()) && isBlankString(modulAuswahlDTO.getFremdId())) {
				return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
			}
		}

		Optional<String> institutionId = anmeldungService.getLatestAnmeldung(refnr)
			.map(AbstractInstitutionPeriodeEntity::getInstitutionId);

		if (institutionId.isEmpty()) {
			return Uni.createFrom().item(Response.status(Status.NOT_FOUND).build());
		}

		Optional<Client> client = clientService.findActive(new ClientId(clientName, institutionId.get()));

		if (client.isEmpty()) {
			return Uni.createFrom().item(Response.status(Status.FORBIDDEN).build());
		}

		TagesschuleBestaetigungEventDTO tagesschuleBestaetigungEventDTO =
			objectMapper.convertValue(bestaetigungDTO, TagesschuleBestaetigungEventDTO.class);

		if (!tagesschuleBestaetigungEventDTO.getRefnr().equals(refnr)) {
			return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
		}

		LOG.debug("generating message");
		CompletionStage<Response> acked =
			anmeldungKafkaEventProducer.process(tagesschuleBestaetigungEventDTO, client.get())
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

	@GET
	@Path("/tarife/refnr/{refnr}")
	@Operation(
		summary = "Tarife einer Tagesschulen-Betreuung",
		description = "Gibt die Tarife einer bestätigten Tagesschulen-Betreuung zurück."
			+ "\n\n"
			+ "Die Tarife sind abhängig von der finanziellen Situation der Familie und müssen durch die Gemeinde "
			+ "verfügt werden.")
	@SecurityRequirement(name = "OAuth2", scopes = "tagesschule")
	@APIResponse(responseCode = "200",
		content = @Content(schema = @Schema(implementation = TagesschuleTarifeDTO.class)))
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("tagesschule")
	public Response getTarife(@NotEmpty @PathParam("refnr") String refnr) {
		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Tagesschule Tarife accessed by '{}' with clientName '{}', roles '{}'. refNr '{}'",
			userName,
			clientName,
			groups,
			refnr);

		Optional<Anmeldung> anmeldung = anmeldungService.getLatestAnmeldung(refnr);

		if (anmeldung.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}

		ClientId clientId = new ClientId(clientName, anmeldung.get().getInstitutionId());
		Optional<Client> client = clientService.findActive(clientId);

		if (client.isEmpty()) {
			return Response.status(Status.FORBIDDEN).build();
		}

		return Response.ok(convertToTagesschuleTarifeDTO(anmeldung.get())).build();
	}

	@GET
	@Path("/module/institution/{institutionId}/periode/{periodeVon}")
	@Operation(
		summary = "Module einer Tagesschule",
		description = "Gibt die Module der Tagesschule in der Periode an.")
	@SecurityRequirement(name = "OAuth2", scopes = "tagesschule")
	@APIResponse(responseCode = "200",
		content = @Content(schema = @Schema(implementation = TagesschuleModuleDTO.class)))
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("tagesschule")
	public Response getModule(
		@NotEmpty @PathParam("institutionId") String institutionId,
		@NotNull @Parameter(description = "Jahr (vierstellig), in welchem das Schuljahr beginnt.")
		@PathParam("periodeVon") Integer periodeVon) {

		String clientName = jsonWebToken.getClaim(CLIENT_ID);
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Tagesschule Module accessed by '{}' with clientName '{}', roles '{}'. institutionId '{}'",
			userName,
			clientName,
			groups,
			institutionId);

		Response response = clientService.find(new ClientId(clientName, institutionId))
			.map(client -> toClientModuleResponse(client, periodeVon))
			// Institution not found for given client
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());

		return response;
	}

	@Nonnull
	private Response toClientModuleResponse(@Nonnull Client client, @Nonnull Integer periodeVonJahr) {
		if (!client.getActive()) {
			// Client not active (forbidden) for given institution
			return Response.status(Status.FORBIDDEN).build();
		}

		LocalDate periodeVon = LocalDate.of(periodeVonJahr, Month.AUGUST, 1);
		LocalDate periodeBis = periodeVon.plusYears(1).minusDays(1);

		Optional<TagesschuleModuleDTO> tagesschuleModuleDTO = tagesschuleModuleService.find(client, periodeVon, periodeBis);

		return tagesschuleModuleDTO.map(Response::ok)
			.orElseGet(() -> Response.status(Status.NOT_FOUND))
			.build();
	}

	@Nonnull
	private TagesschuleTarifeDTO convertToTagesschuleTarifeDTO(@Nonnull Anmeldung anmeldung) {
		if (anmeldung.getTarife() == null) {
			return new TagesschuleTarifeDTO(anmeldung.getRefnr(), Collections.emptyList(), false);
		}

		TagesschuleTarifeDTO result = objectMapper.convertValue(anmeldung.getTarife(), TagesschuleTarifeDTO.class);
		result.setRefnr(anmeldung.getRefnr());

		return result;
	}
}
