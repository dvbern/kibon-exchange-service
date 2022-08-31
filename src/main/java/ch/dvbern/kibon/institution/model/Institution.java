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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionStatus;
import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.exchange.commons.types.Mandant;
import ch.dvbern.kibon.persistence.BaseEntity;
import ch.dvbern.kibon.tagesschulen.model.TagesschuleModule;
import ch.dvbern.kibon.util.ConstantsUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;

@Table(indexes = {
	@Index(name = "institution_idx1", columnList = "betreuungsArt, status"),
	@Index(name = "institution_sequenceid_idx", columnList = "sequenceid"),
	@Index(name = "institution_mandant_idx", columnList = "mandant") })
@Entity
public class Institution extends BaseEntity {

	@Id
	@Nonnull
	private @NotEmpty String id = "";

	@Nonnull
	private @NotEmpty String name = "";

	@Nullable
	private String traegerschaft = null;

	@Nonnull
	@Enumerated(EnumType.STRING)
	@Column(length = ConstantsUtil.SHORT_COLUMN_SIZE)
	private @NotNull BetreuungsangebotTyp betreuungsArt = BetreuungsangebotTyp.KITA;

	@JsonIgnore
	@Nonnull
	@Enumerated(EnumType.STRING)
	@Column(length = ConstantsUtil.SHORT_COLUMN_SIZE)
	private @NotNull InstitutionStatus status = InstitutionStatus.AKTIV;

	@Nullable
	private LocalDate betreuungsGutscheineAb = null;

	@Nullable
	private LocalDate betreuungsGutscheineBis = null;

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
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN)
	private JsonNode betreuungsAdressen = null;

	/**
	 * A {@link java.util.List<ch.dvbern.kibon.exchange.commons.types.Wochentag>}
	 */
	@SuppressWarnings("UnnecessaryFullyQualifiedName")
	@Nullable
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN)
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
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN)
	private JsonNode altersKategorien = null;

	@Column(nullable = true)
	private boolean subventioniertePlaetze = false;

	@Nullable
	private BigDecimal anzahlPlaetze = null;

	@Nullable
	private BigDecimal anzahlPlaetzeFirmen = null;

	@Nullable
	private BigDecimal auslastungPct = null;

	@Nonnull
	@Generated(GenerationTime.INSERT)
	@Column(nullable = false, insertable = false)
	private Long sequenceId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(length = ConstantsUtil.SHORT_COLUMN_SIZE)
	private Mandant mandant = Mandant.BERN;

	@Nonnull
	private @NotNull LocalDateTime timestampMutiert = LocalDateTime.now();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "institution")
	@Nonnull
	private @NotNull @Valid Set<TagesschuleModule> tagesschuleModule = new HashSet<>();

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
	public InstitutionStatus getStatus() {
		return status;
	}

	public void setStatus(@Nonnull InstitutionStatus status) {
		this.status = status;
	}

	@Nullable
	public LocalDate getBetreuungsGutscheineAb() {
		return betreuungsGutscheineAb;
	}

	public void setBetreuungsGutscheineAb(@Nullable LocalDate betreuungsGutscheineAb) {
		this.betreuungsGutscheineAb = betreuungsGutscheineAb;
	}

	@Nullable
	public LocalDate getBetreuungsGutscheineBis() {
		return betreuungsGutscheineBis;
	}

	public void setBetreuungsGutscheineBis(@Nullable LocalDate betreuungsGutscheineBis) {
		this.betreuungsGutscheineBis = betreuungsGutscheineBis;
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

	@Nonnull
	public Set<TagesschuleModule> getTagesschuleModule() {
		return tagesschuleModule;
	}

	public void setTagesschuleModule(@Nonnull Set<TagesschuleModule> tagesschuleModule) {
		this.tagesschuleModule = tagesschuleModule;
	}

	@Nonnull
	public String getId() {
		return id;
	}

	public void setId(@Nonnull String id) {
		this.id = id;
	}

	@Nullable
	public BigDecimal getAuslastungPct() {
		return auslastungPct;
	}

	public void setAuslastungPct(@Nullable BigDecimal auslastungPct) {
		this.auslastungPct = auslastungPct;
	}

	@Nonnull
	public Long getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(@Nonnull Long sequenceId) {
		this.sequenceId = sequenceId;
	}

	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
	}
}
