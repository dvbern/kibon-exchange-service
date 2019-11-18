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

package ch.dvbern.kibon.verfuegung.model;

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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

@Table(indexes = @Index(name = "verfuegung_idx1", columnList = "institutionId, verfuegtAm"))
@Entity
public class Verfuegung {

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
	private @NotNull LocalDate von = LocalDate.MIN;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDate bis = LocalDate.MIN;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @Min(0) Integer version = -1;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDateTime verfuegtAm = LocalDateTime.MIN;

	@Nonnull
	@Column(nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private @NotNull BetreuungsangebotTyp betreuungsArt = BetreuungsangebotTyp.KITA;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull Long gemeindeBfsNr = -1L;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull String gemeindeName = "";

	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode kind = null;

	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode gesuchsteller = null;

	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode zeitabschnitte = null;

	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode ignorierteZeitabschnitte = null;

	@SuppressWarnings("checkstyle:CyclomaticComplexity")
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Verfuegung)) {
			return false;
		}

		Verfuegung that = (Verfuegung) o;

		return getId() != -1L &&
			getId().equals(that.getId()) &&
			getVersion().equals(that.getVersion()) &&
			getRefnr().equals(that.getRefnr()) &&
			getInstitutionId().equals(that.getInstitutionId()) &&
			getVon().equals(that.getVon()) &&
			getBis().equals(that.getBis()) &&
			getVerfuegtAm().equals(that.getVerfuegtAm()) &&
			getBetreuungsArt() == that.getBetreuungsArt() &&
			getGemeindeBfsNr().equals(that.getGemeindeBfsNr()) &&
			getGemeindeName().equals(that.getGemeindeName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			getRefnr(),
			getInstitutionId(),
			getVon(),
			getBis(),
			getVersion(),
			getVerfuegtAm(),
			getBetreuungsArt(),
			getGemeindeBfsNr(),
			getGemeindeName());
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
	public LocalDate getVon() {
		return von;
	}

	public void setVon(@Nonnull LocalDate von) {
		this.von = von;
	}

	@Nonnull
	public LocalDate getBis() {
		return bis;
	}

	public void setBis(@Nonnull LocalDate bis) {
		this.bis = bis;
	}

	@Nonnull
	public Integer getVersion() {
		return version;
	}

	public void setVersion(@Nonnull Integer version) {
		this.version = version;
	}

	@Nonnull
	public LocalDateTime getVerfuegtAm() {
		return verfuegtAm;
	}

	public void setVerfuegtAm(@Nonnull LocalDateTime verfuegtAm) {
		this.verfuegtAm = verfuegtAm;
	}

	@Nonnull
	public BetreuungsangebotTyp getBetreuungsArt() {
		return betreuungsArt;
	}

	public void setBetreuungsArt(@Nonnull BetreuungsangebotTyp betreuungsArt) {
		this.betreuungsArt = betreuungsArt;
	}

	@Nonnull
	public Long getGemeindeBfsNr() {
		return gemeindeBfsNr;
	}

	public void setGemeindeBfsNr(@Nonnull Long gemeindeBfsNr) {
		this.gemeindeBfsNr = gemeindeBfsNr;
	}

	@Nonnull
	public String getGemeindeName() {
		return gemeindeName;
	}

	public void setGemeindeName(@Nonnull String gemeindeName) {
		this.gemeindeName = gemeindeName;
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

	@Nullable
	public JsonNode getZeitabschnitte() {
		return zeitabschnitte;
	}

	public void setZeitabschnitte(@Nullable JsonNode zeitabschnitte) {
		this.zeitabschnitte = zeitabschnitte;
	}

	@Nullable
	public JsonNode getIgnorierteZeitabschnitte() {
		return ignorierteZeitabschnitte;
	}

	public void setIgnorierteZeitabschnitte(@Nullable JsonNode ignorierteZeitabschnitte) {
		this.ignorierteZeitabschnitte = ignorierteZeitabschnitte;
	}
}
