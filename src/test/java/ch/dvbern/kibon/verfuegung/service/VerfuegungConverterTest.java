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
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

class VerfuegungConverterTest {

	private final VerfuegungConverter converter = new VerfuegungConverter();

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
		Faker faker = new Faker();
		KindDTO kindDTO =
			new KindDTO(faker.name().firstName(), faker.name().lastName(), toLocalDate(faker.date().birthday(1, 3)));
		GesuchstellerDTO gesuchstellerDTO =
			new GesuchstellerDTO(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress());

		VerfuegungEventDTO dto = new VerfuegungEventDTO();
		dto.setKind(kindDTO);
		dto.setGesuchsteller(gesuchstellerDTO);
		dto.setBetreuungsArt(BetreuungsangebotTyp.TAGESFAMILIEN);
		dto.setRefnr("1.1.1");
		dto.setInstitutionId(UUID.randomUUID().toString());
		dto.setVon(toLocalDate(faker.date().past(30, TimeUnit.DAYS)));
		dto.setBis(toLocalDate(faker.date().past(20, TimeUnit.DAYS)));
		dto.setVersion(2);
		dto.setVerfuegtAm(Instant.now());

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
			BigDecimal.valueOf(200),
			BigDecimal.valueOf(15),
			BigDecimal.valueOf(23),
			Zeiteinheit.HOURS
		);

		dto.setZeitabschnitte(Collections.singletonList(zeitabschnittDTO));
		dto.setIgnorierteZeitabschnitte(Collections.emptyList());

		return dto;
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
			.withProperty(
				"zeitabschnitte",
				is(jsonArray(contains(matchesZeitabschnittDTO(dto.getZeitabschnitte().get(0))))))
			.withProperty(
				"ignorierteZeitabschnitte",
				is(jsonArray(hasSize(dto.getIgnorierteZeitabschnitte().size()))));
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
