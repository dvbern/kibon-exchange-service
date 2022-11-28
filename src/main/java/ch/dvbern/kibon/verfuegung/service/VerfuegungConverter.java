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
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.api.common.institution.KibonMandant;
import ch.dvbern.kibon.exchange.commons.util.TimestampConverter;
import ch.dvbern.kibon.exchange.commons.verfuegung.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.KindDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.ZeitabschnittDTO;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static java.util.Objects.requireNonNullElse;

@ApplicationScoped
public class VerfuegungConverter {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper mapper;

	@Nonnull
	public Verfuegung create(@Nonnull VerfuegungEventDTO dto) {
		Verfuegung verfuegung = new Verfuegung();

		verfuegung.setRefnr(dto.getRefnr());
		verfuegung.setInstitutionId(dto.getInstitutionId());
		update(verfuegung, dto);

		return verfuegung;
	}

	public void update(@Nonnull Verfuegung verfuegung, @Nonnull VerfuegungEventDTO dto) {
		verfuegung.setPeriodeVon(dto.getVon());
		verfuegung.setPeriodeBis(dto.getBis());
		verfuegung.setVersion(dto.getVersion());
		verfuegung.setVerfuegtAm(TimestampConverter.of(dto.getVerfuegtAm()));
		verfuegung.setBetreuungsArt(dto.getBetreuungsArt());
		verfuegung.setGemeindeBfsNr(dto.getGemeindeBfsNr());
		verfuegung.setGemeindeName(dto.getGemeindeName());

		verfuegung.setAuszahlungAnEltern(dto.getAuszahlungAnEltern());
		verfuegung.setMandant(requireNonNullElse(KibonMandant.valueOf(dto.getMandant().name()), KibonMandant.BERN));

		verfuegung.setKind(toKind(dto.getKind()));
		verfuegung.setGesuchsteller(toGesuchsteller(dto.getGesuchsteller()));
		verfuegung.setZeitabschnitte(toZeitabschnitte(dto.getZeitabschnitte()));
		verfuegung.setIgnorierteZeitabschnitte(toZeitabschnitte(dto.getIgnorierteZeitabschnitte()));
	}

	@Nonnull
	private ObjectNode toKind(@Nonnull KindDTO kind) {
		return mapper.createObjectNode()
			.put("vorname", kind.getVorname())
			.put("nachname", kind.getNachname())
			.put("geburtsdatum", kind.getGeburtsdatum().toString())
			.put("einschulungTyp", kind.getEinschulungTyp() == null ? null : kind.getEinschulungTyp().name())
			.put("sozialeIndikation", kind.getSozialeIndikation())
			.put("sprachlicheIndikation", kind.getSprachlicheIndikation())
			.put("sprichtMuttersprache", kind.getSprichtMuttersprache())
			.put("ausserordentlicherAnspruch", kind.getAusserordentlicherAnspruch())
			.put("kindAusAsylwesenAngabeElternGemeinde", kind.getKindAusAsylwesenAngabeElternGemeinde())
			.put("keinSelbstbehaltDurchGemeinde", kind.getKeinSelbstbehaltDurchGemeinde());
	}

	@Nonnull
	private ObjectNode toGesuchsteller(@Nonnull GesuchstellerDTO gesuchsteller) {
		return mapper.createObjectNode()
			.put("vorname", gesuchsteller.getVorname())
			.put("nachname", gesuchsteller.getNachname())
			.put("email", gesuchsteller.getEmail());
	}

	@Nonnull
	private ArrayNode toZeitabschnitte(@Nullable List<ZeitabschnittDTO> zeitabschnitte) {
		if (zeitabschnitte == null) {
			return mapper.createArrayNode();
		}

		List<ObjectNode> mapped = zeitabschnitte.stream()
			.map(this::toZeitabschnitt)
			.collect(Collectors.toList());

		return mapper.createArrayNode()
			.addAll(mapped);
	}

	@Nonnull
	private ObjectNode toZeitabschnitt(@Nonnull ZeitabschnittDTO zeitabschnitt) {
		return mapper.createObjectNode()
			.put("von", zeitabschnitt.getVon().toString())
			.put("bis", zeitabschnitt.getBis().toString())
			.put("verfuegungNr", zeitabschnitt.getVerfuegungNr())
			.put("effektiveBetreuungPct", zeitabschnitt.getEffektiveBetreuungPct())
			.put("anspruchPct", zeitabschnitt.getAnspruchPct())
			.put("verguenstigtPct", zeitabschnitt.getVerguenstigtPct())
			.put("vollkosten", zeitabschnitt.getVollkosten())
			.put("betreuungsgutschein", toBetreuungsgutschein(zeitabschnitt))
			.put("minimalerElternbeitrag", zeitabschnitt.getMinimalerElternbeitrag())
			.put("verguenstigung", zeitabschnitt.getVerguenstigung())
			.put("verfuegteAnzahlZeiteinheiten", zeitabschnitt.getVerfuegteAnzahlZeiteinheiten())
			.put("anspruchsberechtigteAnzahlZeiteinheiten", zeitabschnitt.getAnspruchsberechtigteAnzahlZeiteinheiten())
			.put("zeiteinheit", zeitabschnitt.getZeiteinheit().name())
			.put("regelwerk", zeitabschnitt.getRegelwerk().name())
			.put("auszahlungAnEltern", zeitabschnitt.getAuszahlungAnEltern())
			.put("anElternUeberwiesenerBetrag", toAnElternUeberwiesenerBetrag(zeitabschnitt))
			.put("besondereBeduerfnisse", zeitabschnitt.getBesondereBeduerfnisse())
			.put("massgebendesEinkommen", zeitabschnitt.getMassgebendesEinkommen())
			.put("betreuungsgutscheinKanton", zeitabschnitt.getBetreuungsgutscheinKanton())
			.put("babyTarif", zeitabschnitt.getBabyTarif())
			.put("betreuungspensumZeiteinheit", zeitabschnitt.getBetreuungspensumZeiteinheit())
			.put("elternbeitrag", zeitabschnitt.getElternbeitrag());
	}

	@Nonnull
	private static BigDecimal toBetreuungsgutschein(@Nonnull ZeitabschnittDTO zeitabschnitt) {
		return zeitabschnitt.getAuszahlungAnEltern() ? BigDecimal.ZERO : zeitabschnitt.getBetreuungsgutschein();
	}

	@Nonnull
	private static BigDecimal toAnElternUeberwiesenerBetrag(@Nonnull ZeitabschnittDTO zeitabschnitt) {
		return zeitabschnitt.getAuszahlungAnEltern() ? zeitabschnitt.getBetreuungsgutschein() : BigDecimal.ZERO;
	}
}
