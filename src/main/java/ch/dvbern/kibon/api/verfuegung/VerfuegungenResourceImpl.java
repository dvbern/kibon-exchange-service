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
import javax.ws.rs.core.Response;

import ch.dvbern.kibon.exchange.api.institution.model.InstitutionDTO;
import ch.dvbern.kibon.exchange.api.verfuegung.VerfuegungenResource;
import ch.dvbern.kibon.exchange.api.verfuegung.model.ws.VerfuegungDTO;
import ch.dvbern.kibon.exchange.api.verfuegung.model.ws.VerfuegungenDTO;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import ch.dvbern.kibon.verfuegung.service.filter.ClientVerfuegungFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/v1/verfuegungen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VerfuegungenResourceImpl implements VerfuegungenResource {

	private static final Logger LOG = LoggerFactory.getLogger(VerfuegungenResourceImpl.class);

	@Inject
	VerfuegungService verfuegungenService;

	@Inject
	InstitutionService institutionService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	ObjectMapper objectMapper;

	@Inject
	KeycloakSecurityContext keycloakSecurityContext;

	@GET
	@Transactional
	@NoCache
	@Nonnull
	@Override
	@RolesAllowed("user")
	public VerfuegungenDTO getAll(
		@QueryParam("after_id") @Nullable Long afterId,
		@Min(0) @QueryParam("limit") @Nullable Integer limit,
		@QueryParam("$filter") @Nullable String filter) {

		AccessToken token = keycloakSecurityContext.getToken();
		String userName = token.getPreferredUsername();
		String clientName = token.getIssuedFor();
		LOG.info(
			"Verfuegungen accessed by '{}' with clientName '{}' and roles '{}'",
			userName,
			clientName,
			token.getRealmAccess().getRoles());

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

	@Path("/other")
	@Transactional
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@NoCache
	public Response getVerfuegungen() {
		List<Verfuegung> all = verfuegungenService.getAll();

		return Response.ok(all).build();
	}

	@Nonnull
	private VerfuegungDTO convert(@Nonnull ClientVerfuegungDTO model) {
		return objectMapper.convertValue(model, VerfuegungDTO.class);
	}
}
