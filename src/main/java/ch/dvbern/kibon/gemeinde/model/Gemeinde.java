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

package ch.dvbern.kibon.gemeinde.model;

import java.time.LocalDate;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.types.Mandant;
import ch.dvbern.kibon.persistence.BaseEntity;
import ch.dvbern.kibon.util.ConstantsUtil;

@Table
@Entity
public class Gemeinde extends BaseEntity {

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@Column(nullable = false, updatable = true)
	private @NotEmpty String name = "";

	@Nonnull
	@Column(nullable = false, updatable = true)
	private Long bfsNummer;

	@Nonnull
	@Column(nullable = false, updatable = true)
	private LocalDate betreuungsgutscheineAnbietenAb;

	@Nonnull
	@Column(nullable = false, updatable = true)
	private LocalDate gueltigBis;

	@Nonnull
	@Enumerated(EnumType.STRING)
	@Column(length = ConstantsUtil.SHORT_COLUMN_SIZE, updatable = false)
	private Mandant mandant;

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Gemeinde)) {
			return false;
		}

		Gemeinde that = (Gemeinde) o;

		return getId().equals(that.getId()) &&
			getName().equals(that.getName()) &&
			getBfsNummer().equals(that.getBfsNummer()) &&
			getMandant() == that.getMandant() &&
			getBetreuungsgutscheineAnbietenAb().equals(that.getBetreuungsgutscheineAnbietenAb()) &&
			getGueltigBis().equals(that.getGueltigBis());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			getName(),
			getBfsNummer(),
			getMandant(),
			getBetreuungsgutscheineAnbietenAb(),
			getGueltigBis());
	}

	@Nonnull
	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public Long getBfsNummer() {
		return bfsNummer;
	}

	public void setBfsNummer(@Nonnull Long bfsNummer) {
		this.bfsNummer = bfsNummer;
	}

	@Nonnull
	public LocalDate getBetreuungsgutscheineAnbietenAb() {
		return betreuungsgutscheineAnbietenAb;
	}

	public void setBetreuungsgutscheineAnbietenAb(@Nonnull LocalDate betreuungsgutscheineAnbietenAb) {
		this.betreuungsgutscheineAnbietenAb = betreuungsgutscheineAnbietenAb;
	}

	@Nonnull
	public LocalDate getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(@Nonnull LocalDate gueltigBis) {
		this.gueltigBis = gueltigBis;
	}

	@Nonnull
	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(@Nonnull Mandant mandant) {
		this.mandant = mandant;
	}
}
