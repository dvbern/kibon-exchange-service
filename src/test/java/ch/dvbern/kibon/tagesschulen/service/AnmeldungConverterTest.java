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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.tagesschulen.ModulAuswahlDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TarifDTO;
import ch.dvbern.kibon.exchange.commons.types.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.types.KindDTO;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.hamcrest.pojo.IsPojo;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.dvbern.kibon.tagesschulen.service.AnmeldungTagesschuleTestUtil.createPedagogischeTarife;
import static ch.dvbern.kibon.tagesschulen.service.AnmeldungTagesschuleTestUtil.createTagesschuleAnmeldungTestDTO;
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
import static org.junit.Assert.assertNull;

public class AnmeldungConverterTest {

	private final AnmeldungConverter converter = new AnmeldungConverter();

	@BeforeEach
	public void setup() {
		converter.mapper = new ObjectMapper();
	}

	@Test
	public void testCreate() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());

		assertThat(anmeldung, matchesDTO(dto));
		assertNull(anmeldung.getTarifePedagogisch());
		assertNull(anmeldung.getTarifeNichtPedagogisch());
	}

	@Test
	public void testTarife() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		dto.setTarife(createPedagogischeTarife());
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());

		assertThat(anmeldung, matchesDTO(dto));
		assertThat(anmeldung, matchesPedagogischeTarifeInDTO(dto));
	}

	@Nonnull
	private IsPojo<Anmeldung> matchesDTO(@Nonnull TagesschuleAnmeldungEventDTO dto) {
		return pojo(Anmeldung.class)
			.withProperty("kind", matchesKindDTO(dto.getKind()))
			.withProperty("gesuchsteller", matchesGesuchstellerDTO(dto.getGesuchsteller()))
			.withProperty("freigegebenAm", is(dto.getFreigegebenAm()))
			.withProperty("status", is(dto.getStatus()))
			.withProperty("anmeldungZurueckgezogen", is(dto.getAnmeldungZurueckgezogen()))
			.withProperty("refnr", is(dto.getAnmeldungsDetails().getRefnr()))
			.withProperty("eintrittsdatum", is(dto.getAnmeldungsDetails().getEintrittsdatum()))
			.withProperty("planKlasse", is(dto.getAnmeldungsDetails().getPlanKlasse()))
			.withProperty("abholung", is(dto.getAnmeldungsDetails().getAbholung()))
			.withProperty("abweichungZweitesSemester", is(dto.getAnmeldungsDetails().getAbweichungZweitesSemester()))
			.withProperty("bemerkung", is(dto.getAnmeldungsDetails().getBemerkung()))
			.withProperty("periodeVon", is(dto.getPeriodeVon()))
			.withProperty("periodeBis", is(dto.getPeriodeBis()))
			.withProperty("institutionId", is(dto.getInstitutionId()))
			.withProperty(
				"module",
				is(jsonArray(containsInAnyOrder(toMatchers(dto.getAnmeldungsDetails().getModule())))))
			;
	}

	@Nonnull
	private IsPojo<Anmeldung> matchesPedagogischeTarifeInDTO(@Nonnull TagesschuleAnmeldungEventDTO dto) {
		return pojo(Anmeldung.class).withProperty(
			"tarifePedagogisch",
			is(jsonArray(containsInAnyOrder(toTarifeMatchers(dto.getTarife().getTarifePaedagogisch())))))
			.withProperty(
				"tarifeNichtPedagogisch",
				is(jsonArray()))
			;
	}

	@Nonnull
	private Collection<Matcher<? super JsonNode>> toMatchers(@Nonnull List<ModulAuswahlDTO> modulAuswahlDTOS) {
		return modulAuswahlDTOS.stream()
			.map(this::matchesAnmeldungModul)
			.collect(Collectors.toList());
	}

	@Nonnull
	private Matcher<JsonNode> matchesAnmeldungModul(@Nonnull ModulAuswahlDTO modulAuswahlDTO) {
		return is(jsonObject()
			.where("intervall", is(jsonText(modulAuswahlDTO.getIntervall().name())))
			.where("wochentag", is(jsonText(modulAuswahlDTO.getWochentag().name())))
			.where("modulId", is(jsonText(modulAuswahlDTO.getModulId()))));
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
			.where("geburtsdatum", is(jsonText(gesuchsteller.getGeburtsdatum().toString())))
			.where("email", is(jsonText(gesuchsteller.getEmail())))
			.where("geschlecht", is(jsonText(gesuchsteller.getGeschlecht().name())))
			.where("adresse", is(jsonObject()
				.where("ort", is(jsonText(gesuchsteller.getAdresse().getOrt())))
				.where("land", is(jsonText(gesuchsteller.getAdresse().getLand())))
				.where("strasse", is(jsonText(gesuchsteller.getAdresse().getStrasse())))
				.where("hausnummer", is(jsonText(gesuchsteller.getAdresse().getHausnummer())))
				.where("adresszusatz", is(jsonText(gesuchsteller.getAdresse().getAdresszusatz())))
				.where("plz", is(jsonText(gesuchsteller.getAdresse().getPlz())))
			)));
	}

	@Nonnull
	private Collection<Matcher<? super JsonNode>> toTarifeMatchers(@Nonnull List<TarifDTO> tarifDTOS) {
		return tarifDTOS.stream()
			.map(this::matchesTarif)
			.collect(Collectors.toList());
	}

	@Nonnull
	private Matcher<JsonNode> matchesTarif(@Nonnull TarifDTO tarifDTO) {
		return is(jsonObject()
			.where("von", is(jsonText(tarifDTO.getVon().toString())))
			.where("bis", is(jsonText(tarifDTO.getBis().toString())))
			.where("betreuungsKostenProStunde", is(matchesBigDecimal(tarifDTO.getBetreuungsKostenProStunde())))
			.where("betreuungsMinutenProWoche", is(jsonInt(tarifDTO.getBetreuungsMinutenProWoche())))
			.where("totalKostenProWoche", is(matchesBigDecimal(tarifDTO.getTotalKostenProWoche())))
			.where("verpflegungsKostenProWoche", is(matchesBigDecimal(tarifDTO.getVerpflegungsKostenProWoche())))
			.where(
				"verpflegungsKostenVerguenstigung",
				is(matchesBigDecimal(tarifDTO.getVerpflegungsKostenVerguenstigung())))
		);
	}

	@Nonnull
	private Matcher<JsonNode> matchesBigDecimal(@Nonnull BigDecimal expected) {
		// Jackson uses scientific representation of BigDecimal, such that comparesEqual must be used for the match
		return jsonBigDecimal(comparesEqualTo(expected));
	}
}
