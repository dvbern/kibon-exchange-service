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

package ch.dvbern.kibon.institution.model;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Institution {

	@Id
	@Nonnull
	private @NotEmpty String id = "";

	@Nonnull
	private @NotEmpty String name = "";

	@Nullable
	private String traegerschaft = null;

	@Embedded
	@Nonnull
	private @NotNull @Valid Adresse adresse = new Adresse();

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Institution)) {
			return false;
		}

		Institution that = (Institution) o;

		return !getId().isEmpty() &&
			getId().equals(that.getId()) &&
			getName().equals(that.getName()) &&
			Objects.equals(getTraegerschaft(), that.getTraegerschaft()) &&
			getAdresse().equals(that.getAdresse());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getTraegerschaft(), getAdresse());
	}

	@Nonnull
	public String getId() {
		return id;
	}

	public void setId(@Nonnull String id) {
		this.id = id;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nullable
	public String getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(@Nullable String traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	@Nonnull
	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(@Nonnull Adresse adresse) {
		this.adresse = adresse;
	}
}
