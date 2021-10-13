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

package ch.dvbern.kibon.api.tagesschule;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.kibon.exchange.api.common.tagesschule.tarife.TagesschuleTarifeDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.tarife.TarifDTO;

@SuppressWarnings("checkstyle:MagicNumber")
@ApplicationScoped
public class TagesschulenMockResponses {

	@Nonnull
	public TagesschuleTarifeDTO createTarif1(@Nonnull String refnr) {
		TagesschuleTarifeDTO dto = new TagesschuleTarifeDTO();
		dto.setRefnr(refnr);
		dto.setTarifeDefinitivAkzeptiert(false);
		TarifDTO tarif1 = createTarif1();
		tarif1.setVon(LocalDate.of(2020, 11, 1));
		tarif1.setBis(LocalDate.of(2020, 12, 31));

		TarifDTO tarif2 = createTarif1();
		tarif2.setVon(LocalDate.of(2021, 1, 1));
		tarif2.setBetreuungsKostenProStunde(BigDecimal.valueOf(11.9));
		tarif2.setTotalKostenProWoche(BigDecimal.valueOf(22.58));

		dto.getTarifePaedagogisch().add(tarif1);
		dto.getTarifePaedagogisch().add(tarif2);
		dto.getTarifePaedagogisch().add(createTarif1());

		return dto;
	}

	@Nonnull
	public TagesschuleTarifeDTO createTarif2(@Nonnull String refnr) {
		TagesschuleTarifeDTO dto = new TagesschuleTarifeDTO();
		dto.setRefnr(refnr);
		dto.setTarifeDefinitivAkzeptiert(true);
		dto.getTarifePaedagogisch().add(createTarif1());
		dto.getTarifeNichtPaedagogisch().add(createTarif2());

		return dto;
	}

	@Nonnull
	public TarifDTO createTarif1() {
		return new TarifDTO(
			LocalDate.of(2020, 8, 1),
			LocalDate.of(2020, 7, 31),
			350,
			BigDecimal.valueOf(1.84),
			BigDecimal.valueOf(11.5),
			BigDecimal.ZERO,
			BigDecimal.valueOf(22.23));
	}

	@Nonnull
	private TarifDTO createTarif2() {
		return new TarifDTO(
			LocalDate.of(2020, 8, 1),
			LocalDate.of(2020, 7, 31),
			210,
			BigDecimal.valueOf(1.93),
			BigDecimal.valueOf(11),
			BigDecimal.ZERO,
			BigDecimal.valueOf(17.755));
	}
}
