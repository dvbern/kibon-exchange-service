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

package ch.dvbern.kibon.gemeindekennzahlen.service;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;

import ch.dvbern.kibon.exchange.commons.gemeindekennzahlen.GemeindeKennzahlenEventDTO;
import ch.dvbern.kibon.gemeindekennzahlen.model.GemeindeKennzahlen;

@ApplicationScoped
public class GemeindeKennzahlenConverter {

	@Nonnull
	public GemeindeKennzahlen create(@Nonnull GemeindeKennzahlenEventDTO dto) {
		GemeindeKennzahlen gemeindeKennzahlen = new GemeindeKennzahlen();
		gemeindeKennzahlen.setBfsNummer(dto.getBfsNummer());
		gemeindeKennzahlen.setGesuchsperiodeStart(dto.getGesuchsperiodeStart());
		gemeindeKennzahlen.setGesuchsperiodeStop(dto.getGesuchsperiodeStop());
		update(gemeindeKennzahlen, dto);

		return gemeindeKennzahlen;
	}

	public void update(@Nonnull GemeindeKennzahlen gemeindeKennzahlen, @Nonnull GemeindeKennzahlenEventDTO dto) {
		gemeindeKennzahlen.setKontingentierung(dto.getKontingentierung());
		gemeindeKennzahlen.setKontingentierungAusgeschoepft(dto.getKontingentierungAusgeschoepft());
		gemeindeKennzahlen.setAnzahlKinderWarteliste(dto.getAnzahlKinderWarteliste());
		gemeindeKennzahlen.setDauerWarteliste(dto.getDauerWarteliste());
		gemeindeKennzahlen.setLimitierungTfo(dto.getLimitierungTfo());
		gemeindeKennzahlen.setLimitierungKita(dto.getLimitierungKita());
		gemeindeKennzahlen.setErwerbspensumZuschlag(dto.getErwerbspensumZuschlag());
	}
}
