/*
 * Copyright (C) 2021 DV Bern AG, Switzerland
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
