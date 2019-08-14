package ch.dvbern.kibon.api.verfuegung;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.dvbern.kibon.exchange.api.verfuegung.model.ws.VerfuegungDTO;
import ch.dvbern.kibon.exchange.api.verfuegung.model.ws.VerfuegungenDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.annotations.cache.NoCache;

@Path("/verfuegungen")
public class VerfuegungenResource {

	@Inject
	VerfuegungService verfuegungenService;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	ObjectMapper objectMapper;

	@Transactional
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@NoCache
	public VerfuegungenDTO get() {
		VerfuegungenDTO verfuegungenDTO = new VerfuegungenDTO();

		List<ClientVerfuegungDTO> dtos = verfuegungenService.getAllForClient("CSE");

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
