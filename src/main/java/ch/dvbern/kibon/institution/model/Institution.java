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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.util.ConstantsUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

@Entity
public class Institution {

	@Id
	@Nonnull
	private @NotEmpty String id = "";

	@Nonnull
	private @NotEmpty String name = "";

	@Nullable
	private String traegerschaft = null;

	@Nonnull
	@Enumerated(EnumType.STRING)
	private @NotNull BetreuungsangebotTyp betreuungsArt = BetreuungsangebotTyp.KITA;

	@Embedded
	@Nonnull
	private @NotNull @Valid KontaktAngaben kontaktAdresse = new KontaktAngaben();

	/**
	 * A {@link java.util.List<KontaktAngaben>}, but since {@link KontaktAngaben} is an embeddable, we cannot use it
	 * in a List.
	 * To avoid more refactoring, we simply store the entire data set as a JsonNode and hope the no schema migrations
	 * are necessary.
	 */
	@SuppressWarnings("UnnecessaryFullyQualifiedName")
	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb")
	private JsonNode betreuungsAdressen = null;

	/**
	 * A {@link java.util.List<ch.dvbern.kibon.exchange.commons.types.Wochentag>}
	 */
	@SuppressWarnings("UnnecessaryFullyQualifiedName")
	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb")
	private JsonNode oeffnungsTage = null;

	@Nullable
	private LocalTime offenVon = null;

	@Nullable
	private LocalTime offenBis = null;

	@Nullable
	@Column(length = ConstantsUtil.TEXT_AREA_SIZE)
	private String oeffnungsAbweichungen = null;

	/**
	 * A {@link java.util.List<ch.dvbern.kibon.api.institution.familyportal.AltersKategorie>}
	 */
	@SuppressWarnings("UnnecessaryFullyQualifiedName")
	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb")
	private JsonNode altersKategorien = null;

	@Column(nullable = true)
	private boolean subventioniertePlaetze = false;

	@Nullable
	private BigDecimal anzahlPlaetze = null;

	@Nullable
	private BigDecimal anzahlPlaetzeFirmen = null;

	@Nonnull
	@NotNull
	private LocalDateTime timestampMutiert = LocalDateTime.now();

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
			getKontaktAdresse().equals(that.getKontaktAdresse());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getTraegerschaft(), getKontaktAdresse());
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
	public BetreuungsangebotTyp getBetreuungsArt() {
		return betreuungsArt;
	}

	public void setBetreuungsArt(@Nonnull BetreuungsangebotTyp betreuungsArt) {
		this.betreuungsArt = betreuungsArt;
	}

	@Nonnull
	public KontaktAngaben getKontaktAdresse() {
		return kontaktAdresse;
	}

	public void setKontaktAdresse(@Nonnull KontaktAngaben adresse) {
		this.kontaktAdresse = adresse;
	}

	@Nullable
	public JsonNode getBetreuungsAdressen() {
		return betreuungsAdressen;
	}

	public void setBetreuungsAdressen(@Nullable JsonNode betreuungsAdressen) {
		this.betreuungsAdressen = betreuungsAdressen;
	}

	@Nullable
	public JsonNode getOeffnungsTage() {
		return oeffnungsTage;
	}

	public void setOeffnungsTage(@Nullable JsonNode oeffnungsTage) {
		this.oeffnungsTage = oeffnungsTage;
	}

	@Nullable
	public LocalTime getOffenVon() {
		return offenVon;
	}

	public void setOffenVon(@Nullable LocalTime offenVon) {
		this.offenVon = offenVon;
	}

	@Nullable
	public LocalTime getOffenBis() {
		return offenBis;
	}

	public void setOffenBis(@Nullable LocalTime offenBis) {
		this.offenBis = offenBis;
	}

	@Nullable
	public String getOeffnungsAbweichungen() {
		return oeffnungsAbweichungen;
	}

	public void setOeffnungsAbweichungen(@Nullable String oeffnungsAbweichungen) {
		this.oeffnungsAbweichungen = oeffnungsAbweichungen;
	}

	@Nullable
	public JsonNode getAltersKategorien() {
		return altersKategorien;
	}

	public void setAltersKategorien(@Nullable JsonNode altersKategorien) {
		this.altersKategorien = altersKategorien;
	}

	public boolean isSubventioniertePlaetze() {
		return subventioniertePlaetze;
	}

	public void setSubventioniertePlaetze(boolean subventioniertePlaetze) {
		this.subventioniertePlaetze = subventioniertePlaetze;
	}

	@Nullable
	public BigDecimal getAnzahlPlaetze() {
		return anzahlPlaetze;
	}

	public void setAnzahlPlaetze(@Nullable BigDecimal anzahlPlaetze) {
		this.anzahlPlaetze = anzahlPlaetze;
	}

	@Nullable
	public BigDecimal getAnzahlPlaetzeFirmen() {
		return anzahlPlaetzeFirmen;
	}

	public void setAnzahlPlaetzeFirmen(@Nullable BigDecimal anzahlPlaetzeFirmen) {
		this.anzahlPlaetzeFirmen = anzahlPlaetzeFirmen;
	}

	@Nonnull
	public LocalDateTime getTimestampMutiert() {
		return timestampMutiert;
	}

	public void setTimestampMutiert(@Nonnull LocalDateTime timestampMutiert) {
		this.timestampMutiert = timestampMutiert;
	}
}
