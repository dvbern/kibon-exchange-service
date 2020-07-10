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

package ch.dvbern.kibon.api.institution.familyportal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.dvbern.kibon.exchange.api.verfuegung.model.BetreuungsAngebot;

public class FamilyPortalInstitutionDTO implements Serializable {

	private static final long serialVersionUID = -8981532768435583887L;

	@Nonnull
	@Size(min = 1)
	@NotNull
	private String id = "";

	@Nonnull
	@NotNull
	private BetreuungsAngebot betreuungsArt = BetreuungsAngebot.KITA;

	@Nullable
	private String traegerschaft = null;

	@Nonnull
	@Size(min = 1)
	@NotNull
	private String name = "";

	@Nonnull
	@NotNull
	@Valid
	private KontaktAngabenDTO kontaktAdresse = new KontaktAngabenDTO();

	@Nonnull
	@NotNull
	@Valid
	private List<KontaktAngabenDTO> betreuungsAdressen = new ArrayList<>();

	@Nonnull
	@NotNull
	private List<DayOfWeek> oeffnungsTage = new ArrayList<>();

	@Nullable
	private LocalTime offenVon = null;

	@Nullable
	private LocalTime offenBis = null;

	@Nullable
	private String oeffnungsAbweichungen = null;

	@Nonnull
	@NotNull
	private List<AltersKategorie> altersKategorien = new ArrayList<>();

	private boolean subventioniertePlaetze = false;

	@Nullable
	private BigDecimal anzahlPlaetze = null;

	@Nullable
	private BigDecimal anzahlPlaetzeFirmen = null;

	@Nonnull
	@NotNull
	private LocalDateTime timestampMutiert = LocalDateTime.now();

	@Nonnull
	public String getId() {
		return id;
	}

	public void setId(@Nonnull String id) {
		this.id = id;
	}

	@Nonnull
	public BetreuungsAngebot getBetreuungsArt() {
		return betreuungsArt;
	}

	public void setBetreuungsArt(@Nonnull BetreuungsAngebot betreuungsArt) {
		this.betreuungsArt = betreuungsArt;
	}

	@Nullable
	public String getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(@Nullable String traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public KontaktAngabenDTO getKontaktAdresse() {
		return kontaktAdresse;
	}

	public void setKontaktAdresse(@Nonnull KontaktAngabenDTO kontaktAdresse) {
		this.kontaktAdresse = kontaktAdresse;
	}

	@Nonnull
	public List<KontaktAngabenDTO> getBetreuungsAdressen() {
		return betreuungsAdressen;
	}

	public void setBetreuungsAdressen(@Nonnull List<KontaktAngabenDTO> betreuungsAdressen) {
		this.betreuungsAdressen = betreuungsAdressen;
	}

	@Nonnull
	public List<DayOfWeek> getOeffnungsTage() {
		return oeffnungsTage;
	}

	public void setOeffnungsTage(@Nonnull List<DayOfWeek> oeffnungsTage) {
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

	@Nonnull
	public List<AltersKategorie> getAltersKategorien() {
		return altersKategorien;
	}

	public void setAltersKategorien(@Nonnull List<AltersKategorie> altersKategorien) {
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
