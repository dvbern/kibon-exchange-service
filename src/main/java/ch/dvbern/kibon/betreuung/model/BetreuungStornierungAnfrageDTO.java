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

package ch.dvbern.kibon.betreuung.model;

import javax.annotation.Nonnull;

public class BetreuungStornierungAnfrageDTO {

	@Nonnull
	private final String fallNummer;

	@Nonnull
	private final String institutionId;

	public BetreuungStornierungAnfrageDTO(
		@Nonnull String fallNummer,
		@Nonnull String institutionId
	) {
		this.fallNummer = fallNummer;
		this.institutionId = institutionId;
	}

	@Nonnull
	public String getFallNummer() {
		return this.fallNummer;
	}

	@Nonnull
	public String getInstitutionId() { return this.institutionId; }

}
