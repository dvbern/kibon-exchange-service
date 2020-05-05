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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ch.dvbern.kibon.exchange.api.institution.model.InstitutionDTO;
import ch.dvbern.kibon.exchange.api.verfuegung.VerfuegungenResource;
import ch.dvbern.kibon.exchange.api.verfuegung.model.ws.VerfuegungDTO;
import ch.dvbern.kibon.exchange.api.verfuegung.model.ws.VerfuegungenDTO;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import ch.dvbern.kibon.verfuegung.service.filter.ClientVerfuegungFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/v1/verfuegungen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VerfuegungenResourceImpl implements VerfuegungenResource {

	private static final Logger LOG = LoggerFactory.getLogger(VerfuegungenResourceImpl.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	VerfuegungService verfuegungenService;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	InstitutionService institutionService;

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
		summary = "Returns all kiBon Verfuegungen and corresponding institutions which were made available.",
		description = "Returns all kiBon Verfuegungen and corresponding institutions, which were made available "
		+ "to the client in the kiBon application.")
	@Transactional
	@NoCache
	@Nonnull
	@Override
	@RolesAllowed("user")
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load Verfuegungen",
		unit = MetricUnits.MILLISECONDS)
	public VerfuegungenDTO getAll(
		@Parameter(description = "Verfuegungen are ordered by their strictly monotonically increasing ID. Use this "
			+ "parameter to get only Verfuegungen with ID larger after_id. Useful to exclude already fetched "
			+ "Verfuegungen.")
		@QueryParam("after_id") @Nullable Long afterId,
		@Parameter(description = "Limits the maximum result set of Verfuegungen to the specified number")
		@Min(0) @QueryParam("limit") @Nullable Integer limit,
		@Parameter(description = "Extension point for additional filtering, e.g. by institution. Currently not used.")
		@QueryParam("$filter") @Nullable String filter) {

		String clientName = jsonWebToken.getClaim("clientId");
		Set<String> groups = identity.getRoles();
		String userName = identity.getPrincipal().getName();

		LOG.info(
			"Verfuegungen accessed by '{}' with clientName '{}', roles '{}', limit '{}' and after_id '{}'",
			userName,
			clientName,
			groups,
			limit,
			afterId);

		// "filter" parameter is ignored at the moment. Added to API to make adding restrictions easily

		ClientVerfuegungFilter queryFilter = new ClientVerfuegungFilter(clientName, afterId, limit);

		VerfuegungenDTO verfuegungenDTO = new VerfuegungenDTO();

		List<ClientVerfuegungDTO> dtos = verfuegungenService.getAllForClient(queryFilter);

		List<VerfuegungDTO> verfuegungen = dtos.stream()
			.map(this::convert)
			.collect(Collectors.toList());

		verfuegungenDTO.setVerfuegungen(verfuegungen);

		Set<String> institutionIds = verfuegungen.stream()
			.map(VerfuegungDTO::getInstitutionId)
			.collect(Collectors.toSet());

		List<InstitutionDTO> institutionDTOs = institutionService.get(institutionIds);

		verfuegungenDTO.setInstitutionen(institutionDTOs);

		return verfuegungenDTO;
	}

	@Nonnull
	private VerfuegungDTO convert(@Nonnull ClientVerfuegungDTO model) {
		return objectMapper.convertValue(model, VerfuegungDTO.class);
	}
}
