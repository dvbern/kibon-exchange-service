package ch.dvbern.kibon.verfuegung.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.exchange.commons.util.ObjectMapperUtil;
import ch.dvbern.kibon.exchange.commons.verfuegung.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.KindDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.ZeitabschnittDTO;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.javafaker.Faker;
import org.easymock.LogicalOperator;
import org.junit.jupiter.api.Test;

import static java.util.Comparator.nullsLast;
import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.strictMock;
import static org.easymock.EasyMock.verify;

class VerfuegungServiceTest {

	private static final Comparator<Verfuegung> VERFUEGUNG_COMPARATOR = Comparator
		.comparing(Verfuegung::getRefnr)
		.thenComparing(Verfuegung::getInstitutionId)
		.thenComparing(Verfuegung::getVon)
		.thenComparing(Verfuegung::getBis)
		.thenComparing(Verfuegung::getVersion)
		.thenComparing(Verfuegung::getVerfuegtAm)
		.thenComparing(Verfuegung::getBetreuungsArt)
		.thenComparing(Verfuegung::getKind, nullsLast(Comparator.comparing(JsonNode::isObject)))
		.thenComparing(Verfuegung::getGesuchsteller, nullsLast(Comparator.comparing(JsonNode::isObject)))
		.thenComparing(Verfuegung::getZeitabschnitte, nullsLast(Comparator.comparing(JsonNode::isArray)))
		.thenComparing(Verfuegung::getIgnorierteZeitabschnitte, nullsLast(Comparator.comparing(JsonNode::isArray)));

	@Test
	public void testVerfuegungCreated() {
		VerfuegungService service = new VerfuegungService();

		service.mapper = ObjectMapperUtil.MAPPER;
		EntityManager em = strictMock(EntityManager.class);
		service.em = em;

		VerfuegungEventDTO dto = createDTO();

		Verfuegung verfuegung = fromDTO(dto);

		em.persist(cmp(verfuegung, VERFUEGUNG_COMPARATOR, LogicalOperator.EQUAL));
		expectLastCall();

		replay(em);

		service.verfuegungCreated(dto);

		verify(em);
	}

	@Nonnull
	private VerfuegungEventDTO createDTO() {
		Faker faker = new Faker();
		KindDTO kindDTO =
			new KindDTO(faker.name().firstName(), faker.name().lastName(), toLocalDate(faker.date().birthday(1, 3)));
		GesuchstellerDTO gesuchstellerDTO =
			new GesuchstellerDTO(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress());

		VerfuegungEventDTO dto = new VerfuegungEventDTO(kindDTO, gesuchstellerDTO, BetreuungsangebotTyp.TAGESFAMILIEN);
		dto.setRefnr("1.1.1");
		dto.setInstitutionId(UUID.randomUUID().toString());
		dto.setVon(toLocalDate(faker.date().past(30, TimeUnit.DAYS)));
		dto.setBis(toLocalDate(faker.date().past(20, TimeUnit.DAYS)));
		dto.setVersion(2);
		dto.setVerfuegtAm(LocalDateTime.now());

		ZeitabschnittDTO zeitabschnittDTO = new ZeitabschnittDTO(
			dto.getVon(),
			dto.getBis(),
			2,
			BigDecimal.valueOf(80),
			70,
			BigDecimal.valueOf(70),
			BigDecimal.valueOf(2000),
			BigDecimal.valueOf(500),
			BigDecimal.valueOf(300),
			BigDecimal.valueOf(200));

		dto.getZeitabschnitte().add(zeitabschnittDTO);

		return dto;
	}

	@Nonnull
	private Verfuegung fromDTO(@Nonnull VerfuegungEventDTO dto) {
		Verfuegung verfuegung = new Verfuegung();

		verfuegung.setRefnr(dto.getRefnr());
		verfuegung.setInstitutionId(dto.getInstitutionId());
		verfuegung.setVon(dto.getVon());
		verfuegung.setBis(dto.getBis());
		verfuegung.setVersion(dto.getVersion());
		verfuegung.setVerfuegtAm(dto.getVerfuegtAm());
		verfuegung.setBetreuungsArt(dto.getBetreuungsArt());

		verfuegung.setKind(ObjectMapperUtil.MAPPER.valueToTree(dto.getKind()));
		verfuegung.setGesuchsteller(ObjectMapperUtil.MAPPER.valueToTree(dto.getGesuchsteller()));
		verfuegung.setZeitabschnitte(ObjectMapperUtil.MAPPER.valueToTree(dto.getZeitabschnitte()));
		verfuegung.setIgnorierteZeitabschnitte(ObjectMapperUtil.MAPPER.valueToTree(dto.getIgnorierteZeitabschnitte()));

		return verfuegung;
	}

	@Nonnull
	private LocalDate toLocalDate(@Nonnull Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
