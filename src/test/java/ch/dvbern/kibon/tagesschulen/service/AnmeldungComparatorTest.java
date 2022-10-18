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

import java.time.LocalDateTime;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungTarifeDTO;
import ch.dvbern.kibon.exchange.commons.types.Wochentag;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.dvbern.kibon.tagesschulen.service.AnmeldungTagesschuleTestUtil.createTagesschuleAnmeldungTestDTO;
import static ch.dvbern.kibon.tagesschulen.service.AnmeldungTagesschuleTestUtil.createTarife;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.comparator.ComparatorMatcherBuilder.comparedBy;

public class AnmeldungComparatorTest {

	private final AnmeldungConverter converter = new AnmeldungConverter();

	@BeforeEach
	public void setup() {
		converter.mapper = new ObjectMapper();
	}

	@Test
	public void testCompareSameReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		assertThat(anmeldung, comparedBy(Anmeldung.COMPARATOR).comparesEqualTo(sameAnmeldung));
	}

	@Test
	public void testCompareSameReceivedDTOWithNull() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());
		anmeldung.setBemerkung(null);
		anmeldung.setPlanKlasse(null);
		sameAnmeldung.setBemerkung(null);
		sameAnmeldung.setPlanKlasse(null);

		assertThat(anmeldung, comparedBy(Anmeldung.COMPARATOR).comparesEqualTo(sameAnmeldung));
	}

	@Test
	public void testCompareOtherModulInReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		dto.getAnmeldungsDetails().getModule().get(0).setWochentag(Wochentag.FRIDAY);
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		assertThat(anmeldung, not(comparedBy(Anmeldung.COMPARATOR).comparesEqualTo(sameAnmeldung)));
	}

	@Test
	public void testCompareOtherKindDataInReceivedDTO() {

		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		dto.getKind().setVorname("Peter");
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		assertThat(anmeldung, not(comparedBy(Anmeldung.COMPARATOR).comparesEqualTo(sameAnmeldung)));
	}

	@Test
	public void testCompareOtherGesuchstellerDataInReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		dto.getGesuchsteller().setVorname("Peter");
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		assertThat(anmeldung, not(comparedBy(Anmeldung.COMPARATOR).comparesEqualTo(sameAnmeldung)));
	}

	@Test
	public void testCompareOtherBemerkungInReceivedDTO() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		dto.getAnmeldungsDetails().setBemerkung("This is just another Bemerkung");
		Anmeldung sameAnmeldung = converter.create(dto, LocalDateTime.now());

		assertThat(anmeldung, not(comparedBy(Anmeldung.COMPARATOR).comparesEqualTo(sameAnmeldung)));
	}

	@Test
	public void testCompareOtherTarifInReceivedDTO() {
		ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
		ObjectNode tarif1 = mapper.createObjectNode()
			.put("refnr", "1.1.1.1")
			.put("tarifeDefinitivAkzeptiert", false);
		ObjectNode tarif2 = mapper.createObjectNode()
			.put("refnr", "1.1.1.1")
			.put("tarifeDefinitivAkzeptiert", true);

		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		anmeldung.setTarife(tarif1);

		Anmeldung anmeldung2 = converter.create(dto, LocalDateTime.now());
		anmeldung.setTarife(tarif2);

		assertThat(anmeldung, not(comparedBy(Anmeldung.COMPARATOR).comparesEqualTo(anmeldung2)));
	}
}
