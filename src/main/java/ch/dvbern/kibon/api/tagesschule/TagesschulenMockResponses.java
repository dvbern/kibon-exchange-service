/*
 * Copyright (C) 2020 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.api.tagesschule;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.kibon.exchange.api.common.institution.AdresseDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.AbholungTagesschule;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.Geschlecht;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.Intervall;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.ModulAuswahlDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.ModulDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleAnmeldungDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleGesuchstellerDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleKindDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.tarife.TagesschuleTarifeDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.tarife.TarifDTO;

@SuppressWarnings("checkstyle:MagicNumber")
@ApplicationScoped
public class TagesschulenMockResponses {

	private static final String INSTITUTION_ID = UUID.randomUUID().toString();
	private static final LocalDateTime EVENT_TIMESTAMP = LocalDateTime.now().minusDays(3);

	private static final TagesschuleKindDTO KIND_DTO =
		new TagesschuleKindDTO("Simon", "Wälti", LocalDate.of(2014, 4, 13), Geschlecht.MAENNLICH);

	private static final AdresseDTO ADRESSE_DTO = new AdresseDTO("Testweg", "10", null, "3000", "Bern", "CH");

	private static final TagesschuleGesuchstellerDTO ANTRAGSTELLER_DTO = new TagesschuleGesuchstellerDTO(
		"Dagmar",
		"Wälti",
		"test@mailbucket.dvbern.ch",
		Geschlecht.MAENNLICH,
		LocalDate.of(1980, 3, 25),
		"079 000 00 00",
		ADRESSE_DTO);

	public static final ModulDTO MODUL_MORGEN = new ModulDTO(
		UUID.randomUUID().toString(),
		"Morgen",
		"Matin",
		INSTITUTION_ID,
		LocalTime.of(7, 0),
		LocalTime.NOON,
		Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
		Collections.singletonList(Intervall.WOECHENTLICH),
		true,
		BigDecimal.valueOf(15));

	public static final ModulDTO MODUL_MITTAG = new ModulDTO(
		UUID.randomUUID().toString(),
		"Morgen",
		"Midi",
		INSTITUTION_ID,
		LocalTime.NOON,
		LocalTime.of(14, 0),
		Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
		Collections.singletonList(Intervall.WOECHENTLICH),
		true,
		BigDecimal.valueOf(10));

	public static final ModulDTO MODUL_NACHMITTAG = new ModulDTO(
		UUID.randomUUID().toString(),
		"Nachmittag",
		"Après-Midi",
		INSTITUTION_ID,
		LocalTime.of(13, 0),
		LocalTime.of(18, 0),
		Arrays.asList(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY),
		Arrays.asList(Intervall.WOECHENTLICH, Intervall.ALLE_ZWEI_WOCHEN),
		false,
		BigDecimal.valueOf(20));

	@Nonnull
	public TagesschuleAnmeldungDTO createAnmeldung1(@Nonnull String refnr) {
		TagesschuleAnmeldungDTO dto = new TagesschuleAnmeldungDTO();
		dto.setId(1L);
		dto.setInstitutionId(INSTITUTION_ID);
		dto.setRefnr(refnr);
		dto.setEventTimestamp(EVENT_TIMESTAMP);
		dto.setPeriodeVon(LocalDate.of(2020, 8, 1));
		dto.setPeriodeBis(LocalDate.of(2021, 7, 31));
		dto.setEintrittsdatum(LocalDate.of(2020, 11, 1));
		dto.setPlanKlasse(null);
		dto.setAbholung(AbholungTagesschule.ALLEINE_NACH_HAUSE);
		dto.setKind(KIND_DTO);
		dto.setGesuchsteller(ANTRAGSTELLER_DTO);

		dto.setModule(Arrays.asList(
			new ModulAuswahlDTO(MODUL_MORGEN.getId(), DayOfWeek.MONDAY, Intervall.WOECHENTLICH),
			new ModulAuswahlDTO(MODUL_MITTAG.getId(), DayOfWeek.MONDAY, Intervall.WOECHENTLICH),

			new ModulAuswahlDTO(MODUL_MORGEN.getId(), DayOfWeek.TUESDAY, Intervall.WOECHENTLICH),
			new ModulAuswahlDTO(MODUL_NACHMITTAG.getId(), DayOfWeek.TUESDAY, Intervall.ALLE_ZWEI_WOCHEN)
		));

		return dto;
	}

	@Nonnull
	public TagesschuleAnmeldungDTO createAnmeldung2(@Nonnull String refnr) {
		TagesschuleAnmeldungDTO dto = new TagesschuleAnmeldungDTO();
		dto.setId(2L);
		dto.setInstitutionId(INSTITUTION_ID);
		dto.setRefnr(refnr);
		dto.setEventTimestamp(EVENT_TIMESTAMP.plusMinutes(10));
		dto.setPeriodeVon(LocalDate.of(2020, 8, 1));
		dto.setPeriodeBis(LocalDate.of(2021, 7, 31));
		dto.setEintrittsdatum(LocalDate.of(2020, 8, 1));
		dto.setPlanKlasse("3. Klasse");
		dto.setAbholung(null);
		dto.setKind(KIND_DTO);
		dto.setGesuchsteller(ANTRAGSTELLER_DTO);
		dto.setBemerkung("TEST");

		dto.setModule(Arrays.asList(
			new ModulAuswahlDTO(MODUL_MORGEN.getId(), DayOfWeek.MONDAY, Intervall.WOECHENTLICH),

			new ModulAuswahlDTO(MODUL_MORGEN.getId(), DayOfWeek.TUESDAY, Intervall.WOECHENTLICH),
			new ModulAuswahlDTO(MODUL_MITTAG.getId(), DayOfWeek.TUESDAY, Intervall.WOECHENTLICH),
			new ModulAuswahlDTO(MODUL_NACHMITTAG.getId(), DayOfWeek.TUESDAY, Intervall.WOECHENTLICH),

			new ModulAuswahlDTO(MODUL_MORGEN.getId(), DayOfWeek.WEDNESDAY, Intervall.WOECHENTLICH),
			new ModulAuswahlDTO(MODUL_MORGEN.getId(), DayOfWeek.THURSDAY, Intervall.WOECHENTLICH),
			new ModulAuswahlDTO(MODUL_MORGEN.getId(), DayOfWeek.FRIDAY, Intervall.WOECHENTLICH)
		));

		return dto;
	}

	@Nonnull
	public TagesschuleTarifeDTO createTarif1(@Nonnull String refnr) {
		TagesschuleTarifeDTO dto = new TagesschuleTarifeDTO();
		dto.setRefnr(refnr);
		dto.setTarifeDefinitivAkzeptiert(false);
		TarifDTO tarif1 = createTarif1();
		tarif1.setVon(LocalDate.of(2020, 11, 1));
		tarif1.setBis(LocalDate.of(2020, 12, 31));

		TarifDTO tarif2 = createTarif1();
		tarif2.setVon(LocalDate.of(2021, 1, 1));
		tarif2.setBetreuungsKostenProStunde(BigDecimal.valueOf(11.9));
		tarif2.setTotalKostenProWoche(BigDecimal.valueOf(22.58));

		dto.getTarifePaedagogisch().add(tarif1);
		dto.getTarifePaedagogisch().add(tarif2);
		dto.getTarifePaedagogisch().add(createTarif1());

		return dto;
	}

	@Nonnull
	public TagesschuleTarifeDTO createTarif2(@Nonnull String refnr) {
		TagesschuleTarifeDTO dto = new TagesschuleTarifeDTO();
		dto.setRefnr(refnr);
		dto.setTarifeDefinitivAkzeptiert(true);
		dto.getTarifePaedagogisch().add(createTarif1());
		dto.getTarifeNichtPaedagogisch().add(createTarif2());

		return dto;
	}

	@Nonnull
	public TarifDTO createTarif1() {
		return new TarifDTO(
			LocalDate.of(2020, 8, 1),
			LocalDate.of(2020, 7, 31),
			350,
			BigDecimal.valueOf(1.84),
			BigDecimal.valueOf(11.5),
			BigDecimal.ZERO,
			BigDecimal.valueOf(22.23));
	}

	@Nonnull
	private TarifDTO createTarif2() {
		return new TarifDTO(
			LocalDate.of(2020, 8, 1),
			LocalDate.of(2020, 7, 31),
			210,
			BigDecimal.valueOf(1.93),
			BigDecimal.valueOf(11),
			BigDecimal.ZERO,
			BigDecimal.valueOf(17.755));
	}
}
