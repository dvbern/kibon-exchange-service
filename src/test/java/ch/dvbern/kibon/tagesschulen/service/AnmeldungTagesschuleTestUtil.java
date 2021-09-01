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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ch.dvbern.kibon.exchange.commons.tagesschulen.AbholungTagesschule;
import ch.dvbern.kibon.exchange.commons.tagesschulen.ModulAuswahlDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungDetailsDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungStatus;
import ch.dvbern.kibon.exchange.commons.types.AdresseDTO;
import ch.dvbern.kibon.exchange.commons.types.Geschlecht;
import ch.dvbern.kibon.exchange.commons.types.Gesuchsperiode;
import ch.dvbern.kibon.exchange.commons.types.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.types.Intervall;
import ch.dvbern.kibon.exchange.commons.types.KindDTO;

public class AnmeldungTagesschuleTestUtil {

	public static TagesschuleAnmeldungEventDTO createTagesschuleAnmeldungTestDTO() {
		TagesschuleAnmeldungEventDTO tagesschuleAnmeldungEventDTO = new TagesschuleAnmeldungEventDTO();
		tagesschuleAnmeldungEventDTO.setInstitutionId("100");
		tagesschuleAnmeldungEventDTO.setVersion(0);
		tagesschuleAnmeldungEventDTO.setFreigegebenAm(LocalDate.now());
		tagesschuleAnmeldungEventDTO.setAnmeldungZurueckgezogen(false);
		tagesschuleAnmeldungEventDTO.setStatus(TagesschuleAnmeldungStatus.SCHULAMT_ANMELDUNG_AUSGELOEST);
		tagesschuleAnmeldungEventDTO.setKind(createKindDTO());
		tagesschuleAnmeldungEventDTO.setGesuchsperiode(createGesuchsperiodeDTO());
		tagesschuleAnmeldungEventDTO.setAntragstellendePerson(createGesuchstellerDTO());
		tagesschuleAnmeldungEventDTO.setAnmeldungsDetails(createAnmeldungsDetailsDTO());

		return tagesschuleAnmeldungEventDTO;
	}

	private static TagesschuleAnmeldungDetailsDTO createAnmeldungsDetailsDTO() {
		TagesschuleAnmeldungDetailsDTO tagesschuleAnmeldungDetailsDTO = new TagesschuleAnmeldungDetailsDTO();
		tagesschuleAnmeldungDetailsDTO.setAbholung(AbholungTagesschule.ALLEINE_NACH_HAUSE);
		tagesschuleAnmeldungDetailsDTO.setBemerkung("Ich habe eine Bemerkung");
		tagesschuleAnmeldungDetailsDTO.setEintrittsdatum(LocalDate.of(2021, 8, 1));
		tagesschuleAnmeldungDetailsDTO.setRefnr("21.000001.001.1.1");
		tagesschuleAnmeldungDetailsDTO.setPlanKlasse("3a");
		tagesschuleAnmeldungDetailsDTO.setAbweichungZweitesSemester(false);
		tagesschuleAnmeldungDetailsDTO.setModulSelection(createModulAuswahlDTOList());

		return tagesschuleAnmeldungDetailsDTO;
	}

	private static List<ModulAuswahlDTO> createModulAuswahlDTOList() {
		List<ModulAuswahlDTO> modulAuswahlDTOList = new ArrayList<>();
		modulAuswahlDTOList.add(createModulAuswahlDTO("1"));
		modulAuswahlDTOList.add(createModulAuswahlDTO("2"));
		return modulAuswahlDTOList;
	}

	private static ModulAuswahlDTO createModulAuswahlDTO(String moduleId) {
		ModulAuswahlDTO modulAuswahlDTO = new ModulAuswahlDTO();
		modulAuswahlDTO.setModulId(moduleId);
		modulAuswahlDTO.setIntervall(Intervall.WOECHENTLICH);
		modulAuswahlDTO.setWeekday(1);
		return modulAuswahlDTO;
	}

	private static GesuchstellerDTO createGesuchstellerDTO() {
		GesuchstellerDTO gesuchstellerDTO = new GesuchstellerDTO();
		gesuchstellerDTO.setGeschlecht(Geschlecht.MAENNLICH);
		gesuchstellerDTO.setGeburtsdatum(LocalDate.of(1990, 10, 10));
		gesuchstellerDTO.setNachname("Antragsteller Nachname");
		gesuchstellerDTO.setVorname("Antragsteller Vorname");
		gesuchstellerDTO.setEmail("email@test.dvbern.ch");
		gesuchstellerDTO.setAdresse(createAdresseDTO());
		return gesuchstellerDTO;
	}

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

	private static Gesuchsperiode createGesuchsperiodeDTO() {
		Gesuchsperiode gesuchsperiode = new Gesuchsperiode();
		gesuchsperiode.setGueltigAb(LocalDate.of(2021, 8, 1));
		gesuchsperiode.setGueltigBis(LocalDate.of(2022, 7, 31));
		gesuchsperiode.setId("101");
		return gesuchsperiode;
	}

	public static KindDTO createKindDTO() {
		KindDTO kindDTO = new KindDTO();
		kindDTO.setGeburtsdatum(LocalDate.of(2010, 1, 7));
		kindDTO.setGeschlecht(Geschlecht.MAENNLICH);
		kindDTO.setNachname("Kind Nachname");
		kindDTO.setVorname("Kind Vorname");
		return kindDTO;
	}
}
