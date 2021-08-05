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

package ch.dvbern.kibon.betreuung.service;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.betreuung.model.BetreuungAnfrage;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungAnfrageEventDTO;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.KindDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class BetreuungAnfrageConverter {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper mapper;

	@Nonnull
	public BetreuungAnfrage create(@Nonnull BetreuungAnfrageEventDTO dto, @Nonnull LocalDateTime eventTimestamp) {
		BetreuungAnfrage betreuungAnfrage = new BetreuungAnfrage();
		betreuungAnfrage.setRefnr(dto.getRefnr());
		betreuungAnfrage.setInstitutionId(dto.getInstitutionId());
		betreuungAnfrage.setPeriodeBis(dto.getPeriodeBis());
		betreuungAnfrage.setPeriodeVon(dto.getPeriodeVon());
		betreuungAnfrage.setAbgelehntVonGesuchsteller(dto.getAbgelehntVonGesuchsteller());
		betreuungAnfrage.setBetreuungsArt(dto.getBetreuungsArt());
		betreuungAnfrage.setKind(toKind(dto.getKind()));
		betreuungAnfrage.setGesuchsteller(toGesuchsteller(dto.getGesuchsteller()));
		betreuungAnfrage.setEventTimestamp(eventTimestamp);

		return betreuungAnfrage;
	}

	@Nonnull
	private ObjectNode toKind(@Nonnull KindDTO kind) {
		return mapper.createObjectNode()
			.put("vorname", kind.getVorname())
			.put("nachname", kind.getNachname())
			.put("geburtsdatum", kind.getGeburtsdatum().toString());
	}

	@Nonnull
	private ObjectNode toGesuchsteller(@Nonnull GesuchstellerDTO gesuchsteller) {
		return mapper.createObjectNode()
			.put("vorname", gesuchsteller.getVorname())
			.put("nachname", gesuchsteller.getNachname())
			.put("email", gesuchsteller.getEmail());
	}

}
