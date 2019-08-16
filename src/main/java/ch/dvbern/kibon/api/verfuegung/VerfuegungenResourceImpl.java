package ch.dvbern.kibon.api.verfuegung;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

import ch.dvbern.kibon.exchange.api.verfuegung.VerfuegungenResource;
import ch.dvbern.kibon.exchange.api.verfuegung.model.ws.VerfuegungDTO;
import ch.dvbern.kibon.exchange.api.verfuegung.model.ws.VerfuegungenDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import ch.dvbern.kibon.verfuegung.service.filter.ClientVerfuegungFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.annotations.cache.NoCache;

@Path("/v1/verfuegungen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VerfuegungenResourceImpl implements VerfuegungenResource {

	@Inject
	VerfuegungService verfuegungenService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	ObjectMapper objectMapper;

	@Transactional
	@NoCache
	@Nonnull
	@Override
	public VerfuegungenDTO getAll(
		@QueryParam("after_id") @Nullable Long afterId,
		@Min(0) @QueryParam("limit") @Nullable Integer limit,
		@QueryParam("$filter") @Nullable String filter) {

		// "filter" parameter is ignored at the moment. Added to API to make adding restrictions easily

		ClientVerfuegungFilter queryFilter = new ClientVerfuegungFilter("CSE", afterId, limit);

		VerfuegungenDTO verfuegungenDTO = new VerfuegungenDTO();

		List<ClientVerfuegungDTO> dtos = verfuegungenService.getAllForClient(queryFilter);

		List<VerfuegungDTO> collect = dtos.stream()
			.map(this::convert)
			.collect(Collectors.toList());

		verfuegungenDTO.setVerfuegungen(collect);

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
