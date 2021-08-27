/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.institution.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.institution.AltersKategorie;
import ch.dvbern.kibon.exchange.commons.institution.GemeindeDTO;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionStatus;
import ch.dvbern.kibon.exchange.commons.institution.KontaktAngabenDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.ModulDTO;
import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.exchange.commons.types.Gesuchsperiode;
import ch.dvbern.kibon.exchange.commons.types.ModulIntervall;
import ch.dvbern.kibon.exchange.commons.util.TimeConverter;
import ch.dvbern.kibon.exchange.commons.util.TimestampConverter;
import ch.dvbern.kibon.institution.model.Gemeinde;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.model.KontaktAngaben;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import static ch.dvbern.kibon.exchange.commons.types.Wochentag.FRIDAY;
import static ch.dvbern.kibon.exchange.commons.types.Wochentag.MONDAY;
import static ch.dvbern.kibon.exchange.commons.types.Wochentag.THURSDAY;
import static ch.dvbern.kibon.exchange.commons.types.Wochentag.TUESDAY;
import static ch.dvbern.kibon.exchange.commons.types.Wochentag.WEDNESDAY;

public final class InstitutionTestUtil {

	public static final Comparator<Gemeinde> GEMEINDE_COMPARATOR = Comparator
		.comparing(Gemeinde::getName, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(Gemeinde::getBfsNummer, Comparator.nullsLast(Comparator.naturalOrder()));

	public static final Comparator<KontaktAngaben> ADRESSE_COMPARATOR = Comparator
		.comparing(KontaktAngaben::getAnschrift, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(KontaktAngaben::getStrasse)
		.thenComparing(KontaktAngaben::getHausnummer, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(KontaktAngaben::getAdresszusatz, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(KontaktAngaben::getPlz)
		.thenComparing(KontaktAngaben::getOrt)
		.thenComparing(KontaktAngaben::getLand)
		.thenComparing(KontaktAngaben::getGemeinde, Comparator.nullsLast(GEMEINDE_COMPARATOR))
		.thenComparing(KontaktAngaben::getEmail, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(KontaktAngaben::getTelefon, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(KontaktAngaben::getWebseite, Comparator.nullsLast(Comparator.naturalOrder()));

	public static final Comparator<Institution> INSTITUTION_COMPARATOR = Comparator
		.comparing(Institution::getName)
		.thenComparing(Institution::getTraegerschaft, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(Institution::getKontaktAdresse, ADRESSE_COMPARATOR);

	private static final Faker FAKER = new Faker();

	private InstitutionTestUtil() {
		// util
	}

	@Nonnull
	public static InstitutionEventDTO createInstitutionEvent(boolean isTagesschule) {
		KontaktAngabenDTO adresse = fakeKontaktAngabenDTO();

		return new InstitutionEventDTO(
			"99",
			FAKER.funnyName().name(),
			FAKER.funnyName().name(),
			isTagesschule ? BetreuungsangebotTyp.TAGESSCHULE : BetreuungsangebotTyp.TAGESFAMILIEN,
			adresse,
			Arrays.asList(fakeKontaktAngabenDTO(), fakeKontaktAngabenDTO()),
			InstitutionStatus.AKTIV,
			null,
			null,
			Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY),
			TimeConverter.serialize(LocalTime.of(7, 30)),
			TimeConverter.serialize(LocalTime.of(19, 0)),
			null,
			Collections.singletonList(AltersKategorie.VORSCHULE),
			true,
			BigDecimal.valueOf(13.23),
			BigDecimal.valueOf(7.13),
			TimestampConverter.fromLocalDateTime(LocalDateTime.now()),
			isTagesschule ? createModule() : null
		);
	}

	private static List<ModulDTO> createModule() {
		List<ModulDTO> modulDTOS = new ArrayList<>();
		ModulDTO modulDTO = new ModulDTO(
			"100",
			"bezeichnungDE",
			"bezeichnungFR",
			TimeConverter.serialize(LocalTime.of(7, 30)),
			TimeConverter.serialize(LocalTime.of(8, 30)),
			Arrays.asList(1, 2) ,
			ModulIntervall.WOECHENTLICH,
			true,
			new BigDecimal(10.5),
			new Gesuchsperiode("101",
				LocalDate.of(2020,8,1),
				LocalDate.of(2020,8,1))

		);
		modulDTOS.add(modulDTO);
		return modulDTOS;
	}

	@Nonnull
	private static KontaktAngabenDTO fakeKontaktAngabenDTO() {
		GemeindeDTO gemeinde = new GemeindeDTO("Bern", null);

		KontaktAngabenDTO adresse = new KontaktAngabenDTO(
			FAKER.company().name(),
			FAKER.address().streetName(),
			FAKER.address().buildingNumber(),
			null,
			FAKER.address().zipCode(),
			FAKER.address().cityName(),
			FAKER.address().countryCode(),
			gemeinde,
			FAKER.internet().emailAddress(),
			FAKER.phoneNumber().phoneNumber(),
			FAKER.internet().url()
		);
		return adresse;
	}

	@Nonnull
	public static Institution fromDTO(@Nonnull InstitutionEventDTO dto) {
		Institution institution = new Institution();
		institution.setName(dto.getName());
		institution.setTraegerschaft(dto.getTraegerschaft());
		InstitutionConverter converter = new InstitutionConverter();
		converter.mapper = new ObjectMapper();

		converter.update(institution, dto);

		return institution;
	}
}
