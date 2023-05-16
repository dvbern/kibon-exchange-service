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

package ch.dvbern.kibon.tagesschulen.model;

import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.persistence.BaseEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Table(uniqueConstraints = @UniqueConstraint(name = "tagesschule_module_uc1",
	columnNames = { "institution_id", "periodevon", "periodebis" }))
@Entity
public class TagesschuleModule extends BaseEntity {

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "institution_fk"), nullable = false, updatable = false)
	private @NotNull Institution institution;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDate periodeVon = LocalDate.MIN;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDate periodeBis = LocalDate.MIN;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parent")
	@Nonnull
	private final @Valid Set<Modul> module = new HashSet<>();

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof TagesschuleModule)) {
			return false;
		}

		TagesschuleModule that = (TagesschuleModule) o;

		return getId() != -1L
			&& getId().equals(that.getId())
			&& getInstitution().equals(that.getInstitution())
			&& getPeriodeVon().equals(that.getPeriodeVon())
			&& getPeriodeBis().equals(that.getPeriodeBis())
			&& getModule().equals(that.getModule());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getInstitution(), getPeriodeVon(), getPeriodeBis(), getModule());
	}

	@Nonnull
	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Nonnull
	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(@Nonnull Institution institution) {
		this.institution = institution;
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

	@Nonnull
	public Set<Modul> getModule() {
		return module;
	}
}
