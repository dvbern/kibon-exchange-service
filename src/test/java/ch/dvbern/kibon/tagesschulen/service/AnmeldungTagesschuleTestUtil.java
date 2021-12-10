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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.tagesschulen.AbholungTagesschule;
import ch.dvbern.kibon.exchange.commons.tagesschulen.ModulAuswahlDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungDetailsDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungStatus;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungTarifeDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TarifDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TarifZeitabschnittDTO;
import ch.dvbern.kibon.exchange.commons.types.AdresseDTO;
import ch.dvbern.kibon.exchange.commons.types.Geschlecht;
import ch.dvbern.kibon.exchange.commons.types.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.types.Intervall;
import ch.dvbern.kibon.exchange.commons.types.KindDTO;
import ch.dvbern.kibon.exchange.commons.types.Wochentag;
import com.github.javafaker.Faker;

public final class AnmeldungTagesschuleTestUtil {

	private static final Faker FAKER = new Faker();

	private AnmeldungTagesschuleTestUtil() {
		// util
	}

	@Nonnull
	public static TagesschuleAnmeldungTarifeDTO createTarife() {
		TarifZeitabschnittDTO zeitabschnittDTO1 = new TarifZeitabschnittDTO(
			LocalDate.of(2020, 8, 1),
			LocalDate.of(2021, 3, 31),
			BigDecimal.valueOf(88231.05),
			BigDecimal.valueOf(2),
			null,
			null
		);

		TarifZeitabschnittDTO zeitabschnittDTO2 = new TarifZeitabschnittDTO(
			LocalDate.of(2021, 4, 1),
			LocalDate.of(2021, 7, 31),
			BigDecimal.valueOf(55645),
			BigDecimal.valueOf(2),
			createTarifDTO(),
			createTarifDTO()
		);

		TagesschuleAnmeldungTarifeDTO tagesschuleAnmeldungTarifeDTO = new TagesschuleAnmeldungTarifeDTO();
		tagesschuleAnmeldungTarifeDTO.setTarifeDefinitivAkzeptiert(true);
		tagesschuleAnmeldungTarifeDTO.setTarifZeitabschnitte(List.of(zeitabschnittDTO1, zeitabschnittDTO2));

		return tagesschuleAnmeldungTarifeDTO;
	}

	@Nonnull
	private static TarifDTO createTarifDTO() {
		return new TarifDTO(
			FAKER.number().numberBetween(1, 10080),
			BigDecimal.valueOf(FAKER.number().randomDouble(2, 5000, 150000)),
			BigDecimal.valueOf(FAKER.number().randomDouble(2, 1, 100)),
			BigDecimal.valueOf(FAKER.number().randomDouble(2, 1, 100)),
			BigDecimal.valueOf(FAKER.number().randomDouble(2, 1, 500))
		);
	}

	@Nonnull
	public static TagesschuleAnmeldungEventDTO createTagesschuleAnmeldungTestDTO() {
		TagesschuleAnmeldungEventDTO tagesschuleAnmeldungEventDTO = new TagesschuleAnmeldungEventDTO();
		tagesschuleAnmeldungEventDTO.setInstitutionId("100");
		tagesschuleAnmeldungEventDTO.setVersion(0);
		tagesschuleAnmeldungEventDTO.setFreigegebenAm(LocalDate.now());
		tagesschuleAnmeldungEventDTO.setAnmeldungZurueckgezogen(false);
		tagesschuleAnmeldungEventDTO.setStatus(TagesschuleAnmeldungStatus.SCHULAMT_ANMELDUNG_AUSGELOEST);
		tagesschuleAnmeldungEventDTO.setKind(createKindDTO());
		tagesschuleAnmeldungEventDTO.setPeriodeVon(LocalDate.of(2021, 8, 1));
		tagesschuleAnmeldungEventDTO.setPeriodeBis(LocalDate.of(2022, 7, 31));
		tagesschuleAnmeldungEventDTO.setGesuchsteller(createGesuchstellerDTO());
		tagesschuleAnmeldungEventDTO.setAnmeldungsDetails(createAnmeldungsDetailsDTO());

		return tagesschuleAnmeldungEventDTO;
	}

	@Nonnull
	private static TagesschuleAnmeldungDetailsDTO createAnmeldungsDetailsDTO() {
		TagesschuleAnmeldungDetailsDTO tagesschuleAnmeldungDetailsDTO = new TagesschuleAnmeldungDetailsDTO();
		tagesschuleAnmeldungDetailsDTO.setAbholung(AbholungTagesschule.ALLEINE_NACH_HAUSE);
		tagesschuleAnmeldungDetailsDTO.setBemerkung("Ich habe eine Bemerkung");
		tagesschuleAnmeldungDetailsDTO.setEintrittsdatum(LocalDate.of(2021, 8, 1));
		tagesschuleAnmeldungDetailsDTO.setRefnr("21.000001.001.1.1");
		tagesschuleAnmeldungDetailsDTO.setPlanKlasse("3a");
		tagesschuleAnmeldungDetailsDTO.setAbweichungZweitesSemester(false);
		tagesschuleAnmeldungDetailsDTO.setModule(createModulAuswahlDTOList());

		return tagesschuleAnmeldungDetailsDTO;
	}

	@Nonnull
	private static List<ModulAuswahlDTO> createModulAuswahlDTOList() {
		List<ModulAuswahlDTO> modulAuswahlDTOList = new ArrayList<>();
		modulAuswahlDTOList.add(createModulAuswahlDTO("1"));
		modulAuswahlDTOList.add(createModulAuswahlDTO("2"));

		return modulAuswahlDTOList;
	}

	@Nonnull
	private static ModulAuswahlDTO createModulAuswahlDTO(String moduleId) {
		ModulAuswahlDTO modulAuswahlDTO = new ModulAuswahlDTO();
		modulAuswahlDTO.setModulId(moduleId);
		modulAuswahlDTO.setIntervall(Intervall.WOECHENTLICH);
		modulAuswahlDTO.setWochentag(Wochentag.MONDAY);

		return modulAuswahlDTO;
	}

	@Nonnull
	private static GesuchstellerDTO createGesuchstellerDTO() {
		GesuchstellerDTO gesuchstellerDTO = new GesuchstellerDTO();
		gesuchstellerDTO.setGeschlecht(Geschlecht.MAENNLICH);
		gesuchstellerDTO.setGeburtsdatum(LocalDate.of(1990, 10, 10));
		gesuchstellerDTO.setNachname("Gesuchsteller Nachname");
		gesuchstellerDTO.setVorname("Gesuchsteller Vorname");
		gesuchstellerDTO.setEmail("email@test.dvbern.ch");
		gesuchstellerDTO.setAdresse(createAdresseDTO());

		return gesuchstellerDTO;
	}

	@Nonnull
	private static AdresseDTO createAdresseDTO() {
		AdresseDTO adresseDTO = new AdresseDTO();
		adresseDTO.setOrt("Fribourg");
		adresseDTO.setLand("CH");
		adresseDTO.setStrasse("Test Strasse");
		adresseDTO.setHausnummer("1");
		adresseDTO.setAdresszusatz("Zusatz");
		adresseDTO.setPlz("1700");

		return adresseDTO;
	}

	@Nonnull
	public static KindDTO createKindDTO() {
		KindDTO kindDTO = new KindDTO();
		kindDTO.setGeburtsdatum(LocalDate.of(2010, 1, 7));
		kindDTO.setGeschlecht(Geschlecht.MAENNLICH);
		kindDTO.setNachname("Kind Nachname");
		kindDTO.setVorname("Kind Vorname");

		return kindDTO;
	}
}
