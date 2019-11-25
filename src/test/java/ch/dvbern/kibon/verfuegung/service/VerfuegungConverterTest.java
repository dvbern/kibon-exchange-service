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

package ch.dvbern.kibon.verfuegung.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.enterprise.context.Destroyed;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.exchange.commons.types.Zeiteinheit;
import ch.dvbern.kibon.exchange.commons.verfuegung.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.KindDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.ZeitabschnittDTO;
import ch.dvbern.kibon.util.LocalDateTimeUtil;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.spotify.hamcrest.pojo.IsPojo;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.spotify.hamcrest.jackson.JsonMatchers.jsonArray;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonBigDecimal;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonInt;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonText;
import static com.spotify.hamcrest.pojo.IsPojo.pojo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

class VerfuegungConverterTest {

	private final VerfuegungConverter converter = new VerfuegungConverter();
	private static final Faker FAKER = new Faker();

	@BeforeEach
	public void setup() {
		converter.mapper = new ObjectMapper();
	}

	@Test
	public void testCreate() {
		VerfuegungEventDTO dto = createDTO();

		Verfuegung verfuegung = converter.create(dto);

		assertThat(verfuegung, matchesDTO(dto));
	}

	@Nonnull
	private VerfuegungEventDTO createDTO() {
		KindDTO kindDTO = createKindDTO();
		GesuchstellerDTO gesuchstellerDTO = createGesuchstellerDTO();

		LocalDate von = toLocalDate(FAKER.date().past(30, TimeUnit.DAYS));
		LocalDate bis = toLocalDate(FAKER.date().past(20, TimeUnit.DAYS));

		VerfuegungEventDTO dto = VerfuegungEventDTO.newBuilder()
			.setKind(kindDTO)
			.setGesuchsteller(gesuchstellerDTO)
			.setBetreuungsArt(BetreuungsangebotTyp.TAGESFAMILIEN)
			.setRefnr("1.1.1")
			.setInstitutionId(UUID.randomUUID().toString())
			.setVon(von)
			.setBis(bis)
			.setVersion(2)
			.setVerfuegtAm(Instant.now())
			.setGemeindeBfsNr(FAKER.number().numberBetween(0, 400))
			.setGemeindeName(FAKER.name().name())
			.setZeitabschnitte(Arrays.asList(createZeitabschnittDTO(von, bis), createZeitabschnittDTO(von, bis)))
			.setIgnorierteZeitabschnitte(Collections.singletonList(createZeitabschnittDTO(von, bis)))
			.build();

		return dto;
	}

	@Nonnull
	private GesuchstellerDTO createGesuchstellerDTO() {
		return GesuchstellerDTO.newBuilder()
			.setVorname(FAKER.name().firstName())
			.setNachname(FAKER.name().lastName())
			.setEmail(FAKER.internet().emailAddress())
			.build();
	}

	@Nonnull
	private KindDTO createKindDTO() {
		return KindDTO.newBuilder()
			.setVorname(FAKER.name().firstName())
			.setNachname(FAKER.name().lastName())
			.setGeburtsdatum(toLocalDate(FAKER.date().birthday(1, 3)))
			.build();
	}

	@Nonnull
	private ZeitabschnittDTO createZeitabschnittDTO(LocalDate von, LocalDate bis) {
		return ZeitabschnittDTO.newBuilder()
			.setVon(von)
			.setBis(bis)
			.setVerfuegungNr(2)
			.setEffektiveBetreuungPct(BigDecimal.valueOf(FAKER.number().randomDouble(2, 0, 1000)))
			.setAnspruchPct(FAKER.number().randomDigit())
			.setVerguenstigtPct(BigDecimal.valueOf(FAKER.number().randomDouble(2, 0, 1000)))
			.setVollkosten(BigDecimal.valueOf(FAKER.number().randomDouble(2, 0, 1000)))
			.setBetreuungsgutschein(BigDecimal.valueOf(FAKER.number().randomDouble(2, 0, 1000)))
			.setMinimalerElternbeitrag(BigDecimal.valueOf(FAKER.number().randomDouble(2, 0, 1000)))
			.setVerguenstigung(BigDecimal.valueOf(FAKER.number().randomDouble(2, 0, 1000)))
			.setVerfuegteAnzahlZeiteinheiten(BigDecimal.valueOf(FAKER.number().randomDouble(2, 0, 1000)))
			.setAnspruchsberechtigteAnzahlZeiteinheiten(BigDecimal.valueOf(FAKER.number().randomDouble(2, 0, 1000)))
			.setZeiteinheit(Zeiteinheit.HOURS)
			.build();
	}

	@Nonnull
	private LocalDate toLocalDate(@Nonnull Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	@Nonnull
	private IsPojo<Verfuegung> matchesDTO(@Nonnull VerfuegungEventDTO dto) {

		return pojo(Verfuegung.class)
			.withProperty("refnr", is(dto.getRefnr()))
			.withProperty("institutionId", is(dto.getInstitutionId()))
			.withProperty("von", is(dto.getVon()))
			.withProperty("bis", is(dto.getBis()))
			.withProperty("version", is(dto.getVersion()))
			.withProperty("verfuegtAm", is(LocalDateTimeUtil.of(dto.getVerfuegtAm())))
			.withProperty("betreuungsArt", is(dto.getBetreuungsArt()))
			.withProperty("kind", matchesKindDTO(dto.getKind()))
			.withProperty("gesuchsteller", matchesGesuchstellerDTO(dto.getGesuchsteller()))
			.where(
				Verfuegung::getZeitabschnitte,
				is(jsonArray(containsInAnyOrder(toMatchers(dto.getZeitabschnitte())))))
			.where(
				Verfuegung::getIgnorierteZeitabschnitte,
				is(jsonArray(containsInAnyOrder(toMatchers(dto.getIgnorierteZeitabschnitte())))));
	}

	@Nonnull
	private Collection<Matcher<? super JsonNode>> toMatchers(List<ZeitabschnittDTO> zeitabschnitte) {
		return zeitabschnitte.stream()
			.map(this::matchesZeitabschnittDTO)
			.collect(Collectors.toList());
	}

	@Nonnull
	private Matcher<JsonNode> matchesKindDTO(@Nonnull KindDTO kind) {
		return is(jsonObject()
			.where("vorname", is(jsonText(kind.getVorname())))
			.where("nachname", is(jsonText(kind.getNachname())))
			.where("geburtsdatum", is(jsonText(kind.getGeburtsdatum().toString()))));
	}

	@Nonnull
	private Matcher<JsonNode> matchesGesuchstellerDTO(@Nonnull GesuchstellerDTO gesuchsteller) {
		return is(jsonObject()
			.where("vorname", is(jsonText(gesuchsteller.getVorname())))
			.where("nachname", is(jsonText(gesuchsteller.getNachname())))
			.where("email", is(jsonText(gesuchsteller.getEmail()))));
	}

	@Nonnull
	private Matcher<JsonNode> matchesZeitabschnittDTO(@Nonnull ZeitabschnittDTO dto) {
		return is(jsonObject()
			.where("von", is(jsonText(dto.getVon().toString())))
			.where("bis", is(jsonText(dto.getBis().toString())))
			.where("verfuegungNr", is(jsonInt(dto.getVerfuegungNr())))
			.where("effektiveBetreuungPct", is(matchesBigDecimal(dto.getEffektiveBetreuungPct())))
			.where("anspruchPct", is(jsonInt((dto.getAnspruchPct()))))
			.where("verguenstigtPct", is(matchesBigDecimal(dto.getVerguenstigtPct())))
			.where("vollkosten", is(matchesBigDecimal(dto.getVollkosten())))
			.where("betreuungsgutschein", is(matchesBigDecimal(dto.getBetreuungsgutschein())))
			.where("minimalerElternbeitrag", is(matchesBigDecimal(dto.getMinimalerElternbeitrag())))
			.where("verguenstigung", is(matchesBigDecimal(dto.getVerguenstigung())))
			.where("verfuegteAnzahlZeiteinheiten", is(matchesBigDecimal(dto.getVerfuegteAnzahlZeiteinheiten())))
			.where(
				"anspruchsberechtigteAnzahlZeiteinheiten",
				is(matchesBigDecimal(dto.getAnspruchsberechtigteAnzahlZeiteinheiten())))
			.where("zeiteinheit", is(jsonText(dto.getZeiteinheit().name())))
		);
	}

	@Nonnull
	private Matcher<JsonNode> matchesBigDecimal(@Nonnull BigDecimal expected) {
		// Jackson uses scientific representation of BigDecimal, such that comparesEqual must be used for the match
		return jsonBigDecimal(comparesEqualTo(expected));
	}
}
