package ch.dvbern.kibon.tagesschulen.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.EntityManager;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.types.ModulIntervall;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.Modul;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static ch.dvbern.kibon.tagesschulen.service.AnmeldungTagesschuleTestUtil.createTagesschuleAnmeldungTestDTO;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

@ExtendWith(EasyMockExtension.class)
public class AnmeldungComparatorTest {

	@TestSubject
	private AnmeldungConverter converter = new AnmeldungConverter();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(MockType.NICE)
	private EntityManager em;

	@BeforeEach
	public void setup() {
		converter.mapper = new ObjectMapper();
	}

	@Test
	public void testCompareSameReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = replayAll();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		Assert.assertTrue(anmeldung.compareTo(sameAnmeldung) == 0);
	}

	@Test
	public void testCompareSameReceivedDTOWithNull() {
		TagesschuleAnmeldungEventDTO dto = replayAll();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());
		anmeldung.setBemerkung(null);
		anmeldung.setPlanKlasse(null);
		sameAnmeldung.setBemerkung(null);
		sameAnmeldung.setPlanKlasse(null);

		Assert.assertTrue(anmeldung.compareTo(sameAnmeldung) == 0);
	}

	@Test
	public void testCompareOtherModulInReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = replayAll();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		dto.getAnmeldungsDetails().getModulSelection().get(0).setWeekday(5);
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		Assert.assertTrue(anmeldung.compareTo(sameAnmeldung) != 0);
	}

	@Test
	public void testCompareOtherKindDataInReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = replayAll();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		dto.getKind().setVorname("Peter");
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		Assert.assertTrue(anmeldung.compareTo(sameAnmeldung) != 0);
	}

	@Test
	public void testCompareOtherGesuchstellerDataInReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = replayAll();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		dto.getAntragstellendePerson().setVorname("Peter");
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		Assert.assertTrue(anmeldung.compareTo(sameAnmeldung) != 0);
	}

	@Test
	public void testCompareOtherBemerkungInReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = replayAll();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		dto.getAnmeldungsDetails().setBemerkung("This is just another Bemerkung");
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		Assert.assertTrue(anmeldung.compareTo(sameAnmeldung) != 0);
	}

	private TagesschuleAnmeldungEventDTO replayAll() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		ch.dvbern.kibon.shared.model.Gesuchsperiode gesuchsperiode = new ch.dvbern.kibon.shared.model.Gesuchsperiode();
		gesuchsperiode.setId(dto.getGesuchsperiode().getId());
		gesuchsperiode.setGueltigAb(dto.getGesuchsperiode().getGueltigAb());
		gesuchsperiode.setGueltigBis(dto.getGesuchsperiode().getGueltigBis());
		expect(em.find(ch.dvbern.kibon.shared.model.Gesuchsperiode.class, dto.getGesuchsperiode().getId())).andReturn(
			gesuchsperiode).times(2);
		dto.getAnmeldungsDetails().getModulSelection().forEach(
			modulAuswahlDTO -> expect(em.find(Modul.class, modulAuswahlDTO.getModulId())).andReturn(createNewModulWithId(
				modulAuswahlDTO.getModulId())).times(2));
		expectLastCall();
		replay(em);
		return dto;
	}

	private Modul createNewModulWithId(String id) {
		Modul modul = new Modul(id);
		modul.setZeitVon(LocalTime.of(8,0));
		modul.setZeitBis(LocalTime.of(9,0));
		modul.setVerpflegungsKosten(new BigDecimal(10.0));
		modul.setPadaegogischBetreut(true);
		modul.setBezeichnungFR("test");
		modul.setBezeichnungDE("test");
		modul.setIntervall(ModulIntervall.WOECHENTLICH);
		modul.setWochentage(converter.mapper.valueToTree(1));
		return modul;
	}

}
