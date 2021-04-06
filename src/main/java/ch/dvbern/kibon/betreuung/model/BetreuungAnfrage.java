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

package ch.dvbern.kibon.betreuung.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

@Table(indexes = @Index(name = "betreuunganfrage_idx1", columnList = "institutionId"))
@Entity
public class BetreuungAnfrage {

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

	@Nonnull
	@Column(nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private @NotNull BetreuungsangebotTyp betreuungsArt = BetreuungsangebotTyp.KITA;

	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode kind = null;

	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode gesuchsteller = null;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull Boolean abgelehntVonGesuchsteller = false;

	@Nonnull
	@Column(updatable = false)
	private @NotNull LocalDateTime eventTimestamp = LocalDateTime.now();


	@SuppressWarnings("checkstyle:CyclomaticComplexity")
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BetreuungAnfrage)) {
			return false;
		}

		BetreuungAnfrage that = (BetreuungAnfrage) o;

		return getId() != -1L &&
			getId().equals(that.getId()) &&
			getRefnr().equals(that.getRefnr()) &&
			getInstitutionId().equals(that.getInstitutionId()) &&
			getPeriodeBis().equals(that.getPeriodeBis()) &&
			getPeriodeVon().equals(that.getPeriodeVon()) &&
			getBetreuungsArt() == that.getBetreuungsArt() &&
			isAbgelehntVonGesuchsteller() == that.isAbgelehntVonGesuchsteller() &&
			getEventTimestamp().equals(that.getEventTimestamp());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			getRefnr(),
			getInstitutionId(),
			getPeriodeVon(),
			getPeriodeBis(),
			getBetreuungsArt(),
			isAbgelehntVonGesuchsteller(),
			getEventTimestamp());
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

	@Nonnull
	public BetreuungsangebotTyp getBetreuungsArt() {
		return betreuungsArt;
	}

	public void setBetreuungsArt(@Nonnull BetreuungsangebotTyp betreuungsArt) {
		this.betreuungsArt = betreuungsArt;
	}

	@Nullable
	public JsonNode getKind() {
		return kind;
	}

	public void setKind(@Nullable JsonNode kind) {
		this.kind = kind;
	}

	@Nullable
	public JsonNode getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(@Nullable JsonNode gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
	}

	public boolean isAbgelehntVonGesuchsteller() {
		return abgelehntVonGesuchsteller;
	}

	public void setAbgelehntVonGesuchsteller(boolean abgelehntVonGesuchsteller) {
		this.abgelehntVonGesuchsteller = abgelehntVonGesuchsteller;
	}

	@Nonnull
	public LocalDateTime getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(@Nonnull LocalDateTime eventTime) {
		this.eventTimestamp = eventTime;
	}
}
