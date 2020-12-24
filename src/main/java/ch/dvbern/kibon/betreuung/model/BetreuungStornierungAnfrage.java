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

import java.time.LocalDateTime;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Table(indexes = @Index(name = "betreuungstornierunganfrage_idx1", columnList = "institutionId"))
@Entity
public class BetreuungStornierungAnfrage {

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
	@Column(updatable = false)
	private @NotNull LocalDateTime eventTimestamp = LocalDateTime.now();

	@SuppressWarnings("checkstyle:CyclomaticComplexity")
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BetreuungStornierungAnfrage)) {
			return false;
		}

		BetreuungStornierungAnfrage that = (BetreuungStornierungAnfrage) o;

		return getId() != -1L &&
			getId().equals(that.getId()) &&
			getRefnr().equals(that.getRefnr()) &&
			getInstitutionId().equals(that.getInstitutionId()) &&
			getEventTimestamp().equals(that.getEventTimestamp());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			getRefnr(),
			getInstitutionId(),
			getEventTimestamp());
	}

	@Nonnull
	public Long getId() { return this.id; }

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
	public LocalDateTime getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(@Nonnull LocalDateTime eventTime) {
		this.eventTimestamp = eventTime;
	}
}
