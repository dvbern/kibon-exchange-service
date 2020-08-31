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

package ch.dvbern.kibon.institution.model;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.persistence.Embeddable;

@Embeddable
public class Gemeinde {

	@Nullable
	private String name;

	@Nullable
	private Long bfsNummer;

	@Nullable
	public String getName() {
		return name;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	@Nullable
	public Long getBfsNummer() {
		return bfsNummer;
	}

	public void setBfsNummer(@Nullable Long bfsNummer) {
		this.bfsNummer = bfsNummer;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Gemeinde)) {
			return false;
		}

		Gemeinde gemeinde = (Gemeinde) o;

		return Objects.equals(getName(), gemeinde.getName()) &&
			Objects.equals(getBfsNummer(), gemeinde.getBfsNummer());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getBfsNummer());
	}
}
