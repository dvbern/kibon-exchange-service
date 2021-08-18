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

import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionStatus;
import ch.dvbern.kibon.exchange.commons.institution.KontaktAngabenDTO;
import ch.dvbern.kibon.exchange.commons.util.TimeConverter;
import ch.dvbern.kibon.exchange.commons.util.TimestampConverter;
import ch.dvbern.kibon.institution.model.Gemeinde;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.model.KontaktAngaben;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.hamcrest.jackson.JsonMatchers;
import com.spotify.hamcrest.pojo.IsPojo;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.dvbern.kibon.institution.service.InstitutionTestUtil.createInstitutionEvent;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonArray;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonNull;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonText;
import static com.spotify.hamcrest.pojo.IsPojo.pojo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.core.Is.is;

class InstitutionConverterTest {

	private final InstitutionConverter converter = new InstitutionConverter();

	@BeforeEach
	public void setup() {
		converter.mapper = new ObjectMapper();
	}

	@Test
	public void testCreate() {
		InstitutionEventDTO dto = createInstitutionEvent();

		Institution institution = converter.create(dto);

		assertThat(institution, matchesDTO(dto));
	}

	@Test
	public void testUpdate() {
		InstitutionEventDTO dto = createInstitutionEvent();
		Institution institution = new Institution();
		// id is not updated, set manually for correct test setup
		institution.setId(dto.getId());

		converter.update(institution, dto);

		assertThat(institution, matchesDTO(dto));
	}

	@Nonnull
	private IsPojo<Institution> matchesDTO(@Nonnull InstitutionEventDTO dto) {
		return pojo(Institution.class)
			.where(Institution::getId, is(dto.getId()))
			.where(Institution::getName, is(dto.getName()))
			.where(Institution::getTraegerschaft, is(dto.getTraegerschaft()))
			.where(Institution::getBetreuungsArt, is(dto.getBetreuungsArt()))
			.where(Institution::getStatus, is(dto.getStatus() == null ? InstitutionStatus.AKTIV : dto.getStatus()))
			.where(Institution::getBetreuungsGutscheineAb, is(dto.getBetreuungsGutscheineAb()))
			.where(Institution::getBetreuungsGutscheineBis, is(dto.getBetreuungsGutscheineBis()))
			.where(Institution::getKontaktAdresse, matchesKontaktAngaben(dto.getAdresse()))
			.where(Institution::getBetreuungsAdressen, matchesBetreuungsAdressen(dto))
			.where(Institution::getOeffnungsTage, matchesOeffnungsTage(dto))
			.where(Institution::getOffenVon, is(TimeConverter.deserialize(dto.getOffenVon())))
			.where(Institution::getOffenBis, is(TimeConverter.deserialize(dto.getOffenBis())))
			.where(Institution::getOeffnungsAbweichungen, is(dto.getOeffnungsAbweichungen()))
			.where(Institution::getAltersKategorien, matchesAlterskategorien(dto))
			.where(Institution::isSubventioniertePlaetze, is(dto.getSubventioniertePlaetze()))
			.where(Institution::getAnzahlPlaetze, comparesEqualTo(dto.getAnzahlPlaetze()))
			.where(Institution::getAnzahlPlaetzeFirmen, comparesEqualTo(dto.getAnzahlPlaetzeFirmen()))
			.where(Institution::getTimestampMutiert, is(TimestampConverter.toLocalDateTime(dto.getTimestampMutiert())))
			;
	}

	@Nonnull
	private IsPojo<KontaktAngaben> matchesKontaktAngaben(@Nonnull KontaktAngabenDTO dto) {
		return pojo(KontaktAngaben.class)
			.where(KontaktAngaben::getAnschrift, is(dto.getAnschrift()))
			.where(KontaktAngaben::getStrasse, is(dto.getStrasse()))
			.where(KontaktAngaben::getHausnummer, is(dto.getHausnummer()))
			.where(KontaktAngaben::getAdresszusatz, is(dto.getAdresszusatz()))
			.where(KontaktAngaben::getPlz, is(dto.getPlz()))
			.where(KontaktAngaben::getOrt, is(dto.getOrt()))
			.where(KontaktAngaben::getLand, is(dto.getLand()))
			.where(KontaktAngaben::getGemeinde, pojo(Gemeinde.class)
				.where(Gemeinde::getName, is(dto.getGemeinde().getName()))
				.where(Gemeinde::getBfsNummer, is(dto.getGemeinde().getBfsNummer()))
			)
			.where(KontaktAngaben::getEmail, is(dto.getEmail()))
			.where(KontaktAngaben::getTelefon, is(dto.getTelefon()))
			.where(KontaktAngaben::getWebseite, is(dto.getWebseite()));
	}

	@Nonnull
	private Matcher<JsonNode> matchesBetreuungsAdressen(@Nonnull InstitutionEventDTO dto) {
		return is(jsonArray(contains(dto.getBetreuungsAdressen().stream()
			.map(this::matchesKontaktAngabenNode)
			.collect(Collectors.toList()))));
	}

	@Nonnull
	private Matcher<JsonNode> matchesAlterskategorien(@Nonnull InstitutionEventDTO dto) {
		return is(jsonArray(contains(dto.getAltersKategorien().stream()
			.map(Enum::name)
			.map(JsonMatchers::jsonText)
			.collect(Collectors.toList()))));
	}

	@Nonnull
	private Matcher<JsonNode> matchesOeffnungsTage(@Nonnull InstitutionEventDTO dto) {
		return is(jsonArray(contains(dto.getOeffnungsTage().stream()
			.map(Enum::name)
			.map(JsonMatchers::jsonText)
			.collect(Collectors.toList()))));
	}

	@Nonnull
	private Matcher<JsonNode> matchesKontaktAngabenNode(@Nonnull KontaktAngabenDTO dto) {
		return is(jsonObject()
			.where("anschrift", is(jsonText(dto.getAnschrift())))
			.where("strasse", is(jsonText(dto.getStrasse())))
			.where("hausnummer", is(jsonText(dto.getHausnummer())))
			.where("adresszusatz", either(jsonNull()).or(jsonText(dto.getAdresszusatz())))
			.where("plz", is(jsonText(dto.getPlz())))
			.where("ort", is(jsonText(dto.getOrt())))
			.where("land", is(jsonText(dto.getLand())))
			.where("gemeinde", is(jsonObject()
				.where("name", is(jsonText(dto.getGemeinde().getName())))
				.where("bfsNummer", is(jsonNull()))
			))
			.where("email", is(jsonText(dto.getEmail())))
			.where("telefon", is(jsonText(dto.getTelefon())))
			.where("webseite", is(jsonText(dto.getWebseite()))));
	}
}
