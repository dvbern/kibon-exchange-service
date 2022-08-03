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

package ch.dvbern.kibon.api.verfuegung;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.dvbern.kibon.api.shared.ClientInstitutionFilterParams;
import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.api.common.institution.InstitutionDTO;
import ch.dvbern.kibon.exchange.api.common.verfuegung.VerfuegungDTO;
import ch.dvbern.kibon.exchange.api.common.verfuegung.VerfuegungenDTO;
import ch.dvbern.kibon.exchange.api.common.verfuegung.ZeitabschnittDTO;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.shared.filter.FilterController;
import ch.dvbern.kibon.shared.filter.FilterControllerFactory;
import ch.dvbern.kibon.util.OpenApiTag;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
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

@Path("/verfuegungen")
@Tag(name = OpenApiTag.BETREUUNGS_GUTSCHEINE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VerfuegungenResource {

	private static final Logger LOG = LoggerFactory.getLogger(VerfuegungenResource.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	VerfuegungService verfuegungenService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	InstitutionService institutionService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ClientService clientService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper objectMapper;

	@SuppressWarnings({ "checkstyle:VisibilityModifier", "CdiInjectionPointsInspection" })
	@Inject
	JsonWebToken jsonWebToken;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	SecurityIdentity identity;

	@GET
	@Operation(
		summary = "Returniert verfügte Betreuungsgutscheine und die davon betroffenen Institutionen.",
		description = "Returniert alle kiBon Verfuegungen und die dazugehörigen Institutionen, für welche der Client "
			+ "in kiBon berechtigt wurde.")
	@SecurityRequirement(name = "OAuth2", scopes = "user")
	@APIResponse(responseCode = "200")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("user")
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load Verfuegungen",
		unit = MetricUnits.MILLISECONDS)
	public VerfuegungenDTO getAll(@BeanParam ClientInstitutionFilterParams filterParams) {

		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Verfuegungen accessed by '{}' with clientName '{}', roles '{}' and filter '{}'",
			userName,
			clientName,
			groups,
			filterParams);

		FilterController<ClientVerfuegung, ClientVerfuegungDTO> queryFilter =
			FilterControllerFactory.verfuegungenFilter(clientName, filterParams);

		List<ClientVerfuegungDTO> dtos = verfuegungenService.getAllForClient(queryFilter);

		List<VerfuegungDTO> verfuegungen = dtos.stream()
			.map(this::convert)
			.collect(Collectors.toList());

		VerfuegungenDTO verfuegungenDTO = new VerfuegungenDTO();
		verfuegungenDTO.setVerfuegungen(verfuegungen);

		Set<String> institutionIds = verfuegungen.stream()
			.map(VerfuegungDTO::getInstitutionId)
			.collect(Collectors.toSet());

		removeZeitabschnitteOutsideGueltigkeit(clientName, verfuegungenDTO, institutionIds);

		List<InstitutionDTO> institutionDTOs = institutionService.get(institutionIds);

		verfuegungenDTO.setInstitutionen(institutionDTOs);

		return verfuegungenDTO;
	}

	@Nonnull
	private VerfuegungDTO convert(@Nonnull ClientVerfuegungDTO model) {
		return objectMapper.convertValue(model, VerfuegungDTO.class);
	}

	void removeZeitabschnitteOutsideGueltigkeit(
		@Nonnull String clientName,
		@Nonnull VerfuegungenDTO verfuegungenDTO,
		@Nonnull Set<String> institutionIds) {

		Map<String, Predicate<ZeitabschnittDTO>> gueltigkeitPredicates = institutionIds.stream()
			.collect(Collectors.toMap(Function.identity(), id -> {
				Client client = clientService.get(new ClientId(clientName, id));

				return outsideGueltigkeitPredicate(client);
			}));

		verfuegungenDTO.getVerfuegungen()
			.removeIf(v -> {
				Predicate<ZeitabschnittDTO> predicate = gueltigkeitPredicates.get(v.getInstitutionId());
				removeZeitabschnitteOutsideGueltigkeit(predicate, v);

				return v.getZeitabschnitte().isEmpty() && v.getIgnorierteZeitabschnitte().isEmpty();
			});
	}

	@Nonnull
	private Predicate<ZeitabschnittDTO> outsideGueltigkeitPredicate(@Nonnull Client client) {
		LocalDate clientAb = client.getGueltigAb();
		LocalDate clientBis = client.getGueltigBis();

		Predicate<ZeitabschnittDTO> isBefore =
			z -> clientAb != null && z.getBis().isBefore(clientAb);
		Predicate<ZeitabschnittDTO> isAfter =
			z -> clientBis != null && z.getVon().isAfter(clientBis);

		return isBefore.or(isAfter);
	}

	private void removeZeitabschnitteOutsideGueltigkeit(
		@Nonnull Predicate<ZeitabschnittDTO> predicate,
		@Nonnull VerfuegungDTO dto) {

		dto.getZeitabschnitte().removeIf(predicate);
		dto.getIgnorierteZeitabschnitte().removeIf(predicate);
	}
}
