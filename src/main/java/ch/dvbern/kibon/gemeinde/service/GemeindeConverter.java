/*
 * Copyright (C) 2022 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.gemeinde.service;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.kibon.exchange.commons.gemeinde.GemeindeEventDTO;
import ch.dvbern.kibon.gemeinde.model.Gemeinde;

@ApplicationScoped
public class GemeindeConverter {

	@Nonnull
	public Gemeinde create(@Nonnull GemeindeEventDTO dto) {
		Gemeinde gemeinde = new Gemeinde();
		gemeinde.setBfsNummer(dto.getBfsNummer());
		gemeinde.setMandant(dto.getMandant());
		update(gemeinde, dto);

		return gemeinde;
	}

	public void update(@Nonnull Gemeinde gemeinde, @Nonnull GemeindeEventDTO dto) {
		gemeinde.setName(dto.getName());
		gemeinde.setBetreuungsgutscheineAnbietenAb(dto.getBetreuungsgutscheineAnbietenAb());
		gemeinde.setGueltigBis(dto.getGueltigBis());
	}
}
