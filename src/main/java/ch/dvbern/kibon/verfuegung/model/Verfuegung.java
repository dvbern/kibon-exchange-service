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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.exchange.commons.types.Mandant;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity;
import ch.dvbern.kibon.util.ConstantsUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import org.hibernate.annotations.Type;

@Table(indexes = {
	@Index(name = "verfuegung_idx1", columnList = "institutionId, verfuegtAm"),
	@Index(name = "verfuegung_mandant_idx", columnList = "mandant"),
	@Index(name = "verfuegung_refnr_version_idx", columnList = "refnr, version")
})
@Entity
public class Verfuegung extends AbstractInstitutionPeriodeEntity {

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

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull Boolean auszahlungAnEltern = false;

	@Nullable
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = false, updatable = false)
	private @NotNull JsonNode kind = null;

	@Nullable
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = false, updatable = false)
	private @NotNull JsonNode gesuchsteller = null;

	@Nullable
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = false, updatable = false)
	private @NotNull JsonNode zeitabschnitte = null;

	@Nullable
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = false, updatable = false)
	private @NotNull JsonNode ignorierteZeitabschnitte = null;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(length = ConstantsUtil.SHORT_COLUMN_SIZE)
	private Mandant mandant = Mandant.BERN;

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

		return super.equals(o)
			&& getVersion().equals(that.getVersion())
			&& getVerfuegtAm().equals(that.getVerfuegtAm())
			&& getBetreuungsArt() == that.getBetreuungsArt()
			&& getGemeindeBfsNr().equals(that.getGemeindeBfsNr())
			&& getGemeindeName().equals(that.getGemeindeName())
			&& getAuszahlungAnEltern().equals(that.getAuszahlungAnEltern());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			Arrays.hashCode(baseHashCodeValues()),
			getVersion(),
			getVerfuegtAm(),
			getBetreuungsArt(),
			getGemeindeBfsNr(),
			getGemeindeName(),
			getAuszahlungAnEltern());
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

	@Nonnull
	public Boolean getAuszahlungAnEltern() {
		return auszahlungAnEltern;
	}

	public void setAuszahlungAnEltern(@Nonnull Boolean auszahlungAnEltern) {
		this.auszahlungAnEltern = auszahlungAnEltern;
	}

	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
	}
}
