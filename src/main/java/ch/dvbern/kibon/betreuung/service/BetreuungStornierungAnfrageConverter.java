/*
 * Copyright (C) 2020 DV Bern AG, Switzerland
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

import ch.dvbern.kibon.betreuung.model.BetreuungStornierungAnfrage;
import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungStornierungAnfrageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class BetreuungStornierungAnfrageConverter {
	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper mapper;

	@Nonnull
	public BetreuungStornierungAnfrage create(@Nonnull BetreuungStornierungAnfrageDTO dto, @Nonnull LocalDateTime eventTimestamp) {
		BetreuungStornierungAnfrage betreuungAnfrage = new BetreuungStornierungAnfrage();
		betreuungAnfrage.setRefnr(dto.getRefnr());
		betreuungAnfrage.setInstitutionId(dto.getInstitutionId());
		betreuungAnfrage.setEventTimestamp(eventTimestamp);

		return betreuungAnfrage;
	}
}
