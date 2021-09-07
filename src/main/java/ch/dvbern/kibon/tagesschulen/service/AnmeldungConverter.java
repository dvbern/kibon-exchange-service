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
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.tagesschulen.ModulAuswahlDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.types.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.types.KindDTO;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class AnmeldungConverter {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper mapper;

	@Nonnull
	public Anmeldung create(@Nonnull TagesschuleAnmeldungEventDTO dto, @Nonnull LocalDateTime eventTimestamp) {
		Anmeldung anmeldung = new Anmeldung();
		anmeldung.setKind(toKind(dto.getKind()));
		anmeldung.setGesuchsteller(toGesuchsteller(dto.getAntragstellendePerson()));
		anmeldung.setFreigegebenAm(dto.getFreigegebenAm());
		anmeldung.setStatus(dto.getStatus());
		anmeldung.setAnmeldungZurueckgezogen(dto.getAnmeldungZurueckgezogen());
		anmeldung.setAnmeldungZurueckgezogen(dto.getAnmeldungZurueckgezogen());
		anmeldung.setRefnr(dto.getAnmeldungsDetails().getRefnr());
		anmeldung.setEintrittsdatum(dto.getAnmeldungsDetails().getEintrittsdatum());
		anmeldung.setPlanKlasse(dto.getAnmeldungsDetails().getPlanKlasse());
		anmeldung.setAbholung(dto.getAnmeldungsDetails().getAbholung());
		anmeldung.setAbweichungZweitesSemester(dto.getAnmeldungsDetails().getAbweichungZweitesSemester());
		anmeldung.setBemerkung(dto.getAnmeldungsDetails().getBemerkung());
		anmeldung.setPeriodeVon(dto.getPeriodeVon());
		anmeldung.setPeriodeBis(dto.getPeriodeBis());
		anmeldung.setInstitutionId(dto.getInstitutionId());
		anmeldung.setEventTimestamp(eventTimestamp);
		anmeldung.setVersion(dto.getVersion());
		anmeldung.setModule(toAnmeldungModule(dto.getAnmeldungsDetails().getModule()));

		return anmeldung;
	}

	@Nonnull
	private ObjectNode toKind(@Nonnull KindDTO kind) {
		return mapper.createObjectNode()
			.put("vorname", kind.getVorname())
			.put("nachname", kind.getNachname())
			.put("geburtsdatum", kind.getGeburtsdatum().toString())
			.put("geschlecht", kind.getGeschlecht().name());
	}

	@Nonnull
	private ObjectNode toGesuchsteller(@Nonnull GesuchstellerDTO gesuchsteller) {
		ObjectNode result = mapper.createObjectNode()
			.put("vorname", gesuchsteller.getVorname())
			.put("nachname", gesuchsteller.getNachname())
			.put("geburtsdatum", gesuchsteller.getGeburtsdatum().toString())
			.put("geschlecht", gesuchsteller.getGeschlecht().name())
			.put("email", gesuchsteller.getEmail());

		result.putObject("adresse")
			.put("ort", gesuchsteller.getAdresse().getOrt())
			.put("land", gesuchsteller.getAdresse().getLand())
			.put("strasse", gesuchsteller.getAdresse().getStrasse())
			.put("hausnummer", gesuchsteller.getAdresse().getHausnummer())
			.put("adresszusatz", gesuchsteller.getAdresse().getAdresszusatz())
			.put("plz", gesuchsteller.getAdresse().getPlz());

		return result;
	}

	@Nonnull
	private ArrayNode toAnmeldungModule(@Nullable List<ModulAuswahlDTO> modulAuswahlDTOS) {
		if (modulAuswahlDTOS == null) {
			return mapper.createArrayNode();
		}

		List<ObjectNode> mapped = modulAuswahlDTOS.stream()
			.map(this::toAnmeldungModul)
			.collect(Collectors.toList());

		return mapper.createArrayNode()
			.addAll(mapped);
	}

	@Nonnull
	private ObjectNode toAnmeldungModul(@Nonnull ModulAuswahlDTO modulAuswahlDTO) {
		return mapper.createObjectNode()
			.put("modulId", modulAuswahlDTO.getModulId())
			.put("wochentag", modulAuswahlDTO.getWochentag().name())
			.put("intervall", modulAuswahlDTO.getIntervall().name());
	}
}
