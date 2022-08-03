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
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.api.shared.ClientInstitutionFilterParams;
import ch.dvbern.kibon.betreuung.facade.BetreuungStornierungAnfrageKafkaEventProducer;
import ch.dvbern.kibon.betreuung.facade.PlatzbestaetigungKafkaEventProducer;
import ch.dvbern.kibon.betreuung.model.BetreuungStornierungAnfrage;
import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrage;
import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrageDTO;
import ch.dvbern.kibon.betreuung.service.BetreuungAnfrageService;
import ch.dvbern.kibon.betreuung.service.BetreuungStornierungAnfrageService;
import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungAnfrageDTO;
import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungAnfragenDTO;
import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungDTO;
import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungStornierungAnfrageDTO;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungEventDTO;
import ch.dvbern.kibon.shared.filter.FilterController;
import ch.dvbern.kibon.shared.filter.FilterControllerFactory;
import ch.dvbern.kibon.util.OpenApiTag;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/betreuung")
@Tag(name = OpenApiTag.BETREUUNGEN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BetreuungResource {

	private static final Logger LOG = LoggerFactory.getLogger(BetreuungResource.class);

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
	BetreuungStornierungAnfrageService betreuungStornierungAnfrageService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ClientService clientService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	PlatzbestaetigungKafkaEventProducer platzbestaetigungProducer;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	BetreuungStornierungAnfrageKafkaEventProducer stornierungAnfrageKafkaEventProducer;

	@GET
	@Operation(
		summary = "Returniert Betreuung-Anfragen",
		description = "Wenn ein Betreuungs-Gesuch bei einer Institution in kiBon eingereicht wird, muss diese den "
			+ "Betreuungs-Platz des Kindes bestätigen."
			+ "\n\n"
			+ "Diese Schnittstelle kann genutzt werden um alle Betreuungs-Anfragen zu laden, welche die Institutionen "
			+ "des Clients betreffen."
			+ "\n\n"
			+ "Betreuungs-Anfragen bleiben immer erhalten - egal ob die Betreuung unterdessen bereits bestätigt wurde."
			+ "\n\n"
			+ "Möchte die Instution die Betreuung ablehnen, so muss sie das weiterhin in kiBon machen.")
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
	public BetreuungAnfragenDTO getAll(@BeanParam ClientInstitutionFilterParams filterParams) {

		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"BetreuungAnfragen accessed by '{}' with clientName '{}', roles '{}', and filter '{}'",
			userName,
			clientName,
			groups,
			filterParams);

		FilterController<ClientBetreuungAnfrage, ClientBetreuungAnfrageDTO> queryFilter =
			FilterControllerFactory.betreuungAnfrageFilter(clientName, filterParams);

		List<ClientBetreuungAnfrageDTO> dtos = betreuungAnfrageService.getAllForClient(queryFilter);

		List<BetreuungAnfrageDTO> betreuungAnfrageDTOs = dtos.stream()
			.map(this::convert)
			.collect(Collectors.toList());

		BetreuungAnfragenDTO result = new BetreuungAnfragenDTO();
		result.setAnfragen(betreuungAnfrageDTOs);

		return result;
	}

	@Nonnull
	private BetreuungAnfrageDTO convert(@Nonnull ClientBetreuungAnfrageDTO model) {
		return objectMapper.convertValue(model, BetreuungAnfrageDTO.class);
	}

	@POST
	@Operation(summary = "Eine Betreuung-Anfrage in kiBon bestätigen oder Betreuungen mutieren.",
		description = "Diese Schnittstelle hat zwei Funktionen:\n"
			+ "1. Automatisierte Bestätigung einer Betreuung-Anfrage.\n"
			+ "2. Mutieren einer Betreuung."
			+ "\n\n"
			+ "kiBon entscheidet selbst, basierend auf dem aktuellen Zustand der Betreuung in kiBon, welche Aktion "
			+ "ausgelöst wird. Gibt es eine offene Betreuungs-Anfrage, dann werden die übergebenen Daten verwendet, "
			+ "um die Betreuung zu bestätigen. Dadurch erhält die Gemeinde die benötigten Angaben der Institution, um "
			+ "über den Antrag zu verfügen."
			+ "\n\n"
			+ "Ansonsten werden die Daten mit der bereits vorhandenen Betreuung verglichen. Hat es Abweichungen, wird "
			+ "eine Mutationsmeldung erstellt, damit durch manuelle Aktion der Gemeinden die Betreuung mutiert werden "
			+ "kann.\n"
			+ "Mutation werden beispielsweise benötigt wenn sich die Betreuungskosten, das Pensum oder der "
			+ "Betreuungszeitraum (Eintritt/Austritt) ändert."
			+ "\n\n"
			+ "### Berücksichtigte Daten\n"
			+ "- Die Stadt Bern geht etwas weiter als der Kanton und übernimmt auch Vergünstigungen für die "
			+ "Mahlzeiten. "
			+ "Dazu müssen Institution der Stadt Bern jedoch die verrechneten Kosten ausweisen und melden.\n"
			+ "- Zeitabschnitte für 2020 werden ignoriert, sofern es sich um eine Mutation handelt. "
			+ "Dies wurde beschlossen, um die bereits versendeten Rechnungen für 2020 nicht zu verändern. "
			+ "Es gibt nämlich verschiedene Ansätze für die Berechnung von dem Betreuungspensum und den Kosten. "
			+ "Beispielsweise, wenn sich innerhalb eines Monats das Pensum ändert, oder beim Umgang mit "
			+ "Kindergartenkinder.\n"
			+ "Bei Betreuung-Anfrage Betätigungen werden aber alle Daten importiert: Da es sich um die erste "
			+ "Betreuungsmeldung handelt, kann es auch noch keine Gutscheine geben, so dass die Rechnung an die Eltern"
			+ " sowieso aktualisiert werden muss.\n"
			+ "- Institutionsadmins können in kiBon definieren, für welchen Zeitraum eine API Client Software Zugriff "
			+ "auf die Daten erhalten soll. Beim Import werden nur Zeitabschnitte innerhalb des berechtigten Zeitraums"
			+ " berücksichtigt."
	)
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200", content = {})
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("user")
	@Timed(name = "betreuungTimer",
		description = "A measure of how long it takes to process BetreuungDTO",
		unit = MetricUnits.MILLISECONDS)
	public Uni<Response> sendBetreuungToKafka(@Nonnull @NotNull @Valid BetreuungDTO betreuungDTO) {
		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Betreuung received by '{}' with clientName '{}', roles '{}'",
			userName,
			clientName,
			groups);

		BetreuungEventDTO betreuungEventDTO = objectMapper.convertValue(betreuungDTO, BetreuungEventDTO.class);

		String institutionId = betreuungEventDTO.getInstitutionId();
		Optional<Client> client = clientService.findActive(new ClientId(clientName, institutionId));

		if (client.isEmpty()) {
			return Uni.createFrom().item(Response.status(Status.FORBIDDEN).build());
		}

		LOG.debug("generating message");
		CompletionStage<Response> acked = platzbestaetigungProducer.process(betreuungEventDTO, client.get())
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
	public Uni<Response> sendBetreuungStornierungToKafka(
		@Nonnull @NotNull @Valid BetreuungStornierungAnfrageDTO betreuungStornierungDTO) {
		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Stornierung received by '{}' with clientName '{}', roles '{}'",
			userName,
			clientName,
			groups);

		String institutionId = betreuungStornierungDTO.getInstitutionId();
		Optional<Client> client = clientService.findActive(new ClientId(clientName, institutionId));

		if (client.isEmpty()) {
			return Uni.createFrom().item(Response.status(Status.FORBIDDEN).build());
		}

		LOG.debug("Persisting anfrage");

		BetreuungStornierungAnfrage betreuungStornierungAnfrage =
			betreuungStornierungAnfrageService.onBetreuungStornierungAnfrageReceived(betreuungStornierungDTO);

		LOG.debug("Generating message");

		CompletionStage<Response> acked =
			stornierungAnfrageKafkaEventProducer.process(betreuungStornierungAnfrage, client.get())
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
}
