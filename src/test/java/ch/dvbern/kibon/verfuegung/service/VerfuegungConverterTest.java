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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.verfuegung.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.KindDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.ZeitabschnittDTO;
import ch.dvbern.kibon.util.LocalDateTimeUtil;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.hamcrest.pojo.IsPojo;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventTestUtil.createDTO;
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
	private Collection<Matcher<? super JsonNode>> toMatchers(@Nonnull List<ZeitabschnittDTO> zeitabschnitte) {
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
			.where("regelwerk", is(jsonText(dto.getRegelwerk().name())))
		);
	}

	@Nonnull
	private Matcher<JsonNode> matchesBigDecimal(@Nonnull BigDecimal expected) {
		// Jackson uses scientific representation of BigDecimal, such that comparesEqual must be used for the match
		return jsonBigDecimal(comparesEqualTo(expected));
	}
}
