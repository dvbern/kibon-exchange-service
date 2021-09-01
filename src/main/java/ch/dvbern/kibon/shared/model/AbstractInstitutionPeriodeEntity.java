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

package ch.dvbern.kibon.shared.model;

import java.time.LocalDate;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class AbstractInstitutionPeriodeEntity {

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotEmpty String refnr = "";

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotEmpty String institutionId = "";

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDate periodeVon = LocalDate.MIN;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDate periodeBis = LocalDate.MIN;

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof AbstractInstitutionPeriodeEntity)) {
			return false;
		}

		AbstractInstitutionPeriodeEntity that = (AbstractInstitutionPeriodeEntity) o;

		return getId() != -1L
			&& getId().equals(that.getId())
			&& getRefnr().equals(that.getRefnr())
			&& getInstitutionId().equals(that.getInstitutionId())
			&& getPeriodeVon().equals(that.getPeriodeVon())
			&& getPeriodeBis().equals(that.getPeriodeBis());
	}

	@Override
	public int hashCode() {
		return Objects.hash(baseHashCodeValues());
	}

	@Nonnull
	protected Object[] baseHashCodeValues() {
		return new Object[] { getRefnr(), getInstitutionId(), getPeriodeVon(), getPeriodeBis() };
	}

	@Nonnull
	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Nonnull
	public String getRefnr() {
		return refnr;
	}

	public void setRefnr(@Nonnull String refnr) {
		this.refnr = refnr;
	}

	@Nonnull
	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(@Nonnull String institutionId) {
		this.institutionId = institutionId;
	}

	@Nonnull
	public LocalDate getPeriodeVon() {
		return periodeVon;
	}

	public void setPeriodeVon(@Nonnull LocalDate periodeVon) {
		this.periodeVon = periodeVon;
	}

	@Nonnull
	public LocalDate getPeriodeBis() {
		return periodeBis;
	}

	public void setPeriodeBis(@Nonnull LocalDate periodeBis) {
		this.periodeBis = periodeBis;
	}
}
