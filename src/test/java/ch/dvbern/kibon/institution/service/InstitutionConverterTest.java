package ch.dvbern.kibon.institution.service;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.exchange.commons.util.ObjectMapperUtil;
import ch.dvbern.kibon.institution.model.Adresse;
import ch.dvbern.kibon.institution.model.Institution;
import com.spotify.hamcrest.pojo.IsPojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.dvbern.kibon.institution.service.InstitutionTestUtil.createInstitutionEvent;
import static com.spotify.hamcrest.pojo.IsPojo.pojo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class InstitutionConverterTest {

	private final InstitutionConverter converter = new InstitutionConverter();

	@BeforeEach
	public void setup() {
		converter.mapper = ObjectMapperUtil.MAPPER;
	}

	@Test
	public void testCreate() {
		InstitutionEventDTO dto = createInstitutionEvent();

		Institution institution = converter.create(dto);

		assertThat(institution, matchesDTO(dto));
	}

	@Test
	public void testUpdate() {
		InstitutionEventDTO dto = createInstitutionEvent();
		Institution institution = new Institution();
		// id is not updated, set manually for correct test setup
		institution.setId(dto.getId());

		converter.update(institution, dto);

		assertThat(institution, matchesDTO(dto));
	}

	@Nonnull
	private IsPojo<Institution> matchesDTO(@Nonnull InstitutionEventDTO dto) {
		return pojo(Institution.class)
			.withProperty("id", is(dto.getId()))
			.withProperty("name", is(dto.getName()))
			.withProperty("traegerschaft", is(dto.getTraegerschaft()))
			.withProperty("adresse", is(pojo(Adresse.class)
				.withProperty("strasse", is(dto.getAdresse().getStrasse()))
				.withProperty("hausnummer", is(dto.getAdresse().getHausnummer()))
				.withProperty("adresszusatz", is(dto.getAdresse().getAdresszusatz()))
				.withProperty("ort", is(dto.getAdresse().getOrt()))
				.withProperty("plz", is(dto.getAdresse().getPlz()))
				.withProperty("land", is(dto.getAdresse().getLand()))
			));
	}
}
