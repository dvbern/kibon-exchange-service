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

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.institution.AdresseDTO;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.model.Adresse;
import ch.dvbern.kibon.institution.model.Institution;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class InstitutionConverter {

	@SuppressWarnings({"CdiInjectionPointsInspection", "checkstyle:VisibilityModifier"})
	@Inject
	ObjectMapper mapper;

	@Nonnull
	public Institution create(@Nonnull InstitutionEventDTO dto) {
		return mapper.convertValue(dto, Institution.class);
	}

	public void update(@Nonnull Institution institution, @Nonnull InstitutionEventDTO dto) {
		institution.setName(dto.getName());
		institution.setTraegerschaft(dto.getTraegerschaft());

		update(institution.getAdresse(), dto.getAdresse());
	}

	private void update(@Nonnull Adresse adresse, @Nonnull AdresseDTO dto) {
		adresse.setStrasse(dto.getStrasse());
		adresse.setHausnummer(dto.getHausnummer());
		adresse.setAdresszusatz(dto.getAdresszusatz());
		adresse.setOrt(dto.getOrt());
		adresse.setPlz(dto.getPlz());
		adresse.setLand(dto.getLand());
	}
}
