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

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.kibon.exchange.commons.tagesschulen.AbholungTagesschule;
import com.fasterxml.jackson.databind.JsonNode;

public class ClientAnmeldungDTO {

	@Nonnull
	private final Long id;

	@Nonnull
	private final String institutionId;

	@Nonnull
	private final String refnr;

	@Nonnull
	private final Integer version;

	@Nonnull
	private final LocalDateTime eventTimestamp;

	@Nonnull
	private final LocalDate periodeVon;

	@Nonnull
	private final LocalDate periodeBis;

	@Nonnull
	private LocalDate eintrittsdatum;

	@Nullable
	private String planKlasse;

	@Nullable
	private AbholungTagesschule abholung;

	@Nonnull
	private Boolean abweichungZweitesSemester;

	@Nullable
	private String bemerkung;

	@Nonnull
	private final JsonNode kind;

	@Nonnull
	private final JsonNode antragsteller;

	@Nonnull
	private JsonNode module;

	@Nonnull
	private boolean anmeldungZurueckgezogen;

	public ClientAnmeldungDTO(
		@Nonnull Long id,
		@Nonnull String institutionId,
		@Nonnull String refnr,
		@Nonnull Integer version,
		@Nonnull LocalDateTime eventTimestamp,
		@Nonnull LocalDate periodeVon,
		@Nonnull LocalDate periodeBis,
		@Nonnull JsonNode kind,
		@Nonnull JsonNode antragsteller,
		@Nullable String planKlasse,
		@Nullable AbholungTagesschule abholung,
		@Nonnull Boolean abweichungZweitesSemester,
		@Nullable String bemerkung,
		@Nonnull Boolean anmeldungZurueckgezogen,
		@Nonnull LocalDate eintrittsdatum,
		@Nonnull JsonNode module) {
		this.id = id;
		this.institutionId = institutionId;
		this.refnr = refnr;
		this.version = version;
		this.eventTimestamp = eventTimestamp;
		this.periodeVon = periodeVon;
		this.periodeBis = periodeBis;
		this.kind = kind;
		this.antragsteller = antragsteller;
		this.planKlasse = planKlasse;
		this.abholung = abholung;
		this.abweichungZweitesSemester = abweichungZweitesSemester;
		this.bemerkung = bemerkung;
		this.anmeldungZurueckgezogen = anmeldungZurueckgezogen;
		this.eintrittsdatum = eintrittsdatum;
		this.module = module;
	}

	@Nonnull
	public Long getId() {
		return id;
	}

	@Nonnull
	public String getInstitutionId() {
		return institutionId;
	}

	@Nonnull
	public String getRefnr() {
		return refnr;
	}

	@Nonnull
	public Integer getVersion() {
		return version;
	}

	@Nonnull
	public LocalDateTime getEventTimestamp() {
		return eventTimestamp;
	}

	@Nonnull
	public LocalDate getPeriodeVon() {
		return periodeVon;
	}

	@Nonnull
	public LocalDate getPeriodeBis() {
		return periodeBis;
	}

	@Nonnull
	public LocalDate getEintrittsdatum() {
		return eintrittsdatum;
	}

	public void setEintrittsdatum(@Nonnull LocalDate eintrittsdatum) {
		this.eintrittsdatum = eintrittsdatum;
	}

	@Nullable
	public String getPlanKlasse() {
		return planKlasse;
	}

	public void setPlanKlasse(@Nullable String planKlasse) {
		this.planKlasse = planKlasse;
	}

	@Nullable
	public AbholungTagesschule getAbholung() {
		return abholung;
	}

	public void setAbholung(@Nullable AbholungTagesschule abholung) {
		this.abholung = abholung;
	}

	@Nonnull
	public Boolean getAbweichungZweitesSemester() {
		return abweichungZweitesSemester;
	}

	public void setAbweichungZweitesSemester(@Nonnull Boolean abweichungZweitesSemester) {
		this.abweichungZweitesSemester = abweichungZweitesSemester;
	}

	@Nullable
	public String getBemerkung() {
		return bemerkung;
	}

	public void setBemerkung(@Nullable String bemerkung) {
		this.bemerkung = bemerkung;
	}

	@Nonnull
	public JsonNode getKind() {
		return kind;
	}

	@Nonnull
	public JsonNode getAntragsteller() {
		return antragsteller;
	}

	public boolean isAnmeldungZurueckgezogen() {
		return anmeldungZurueckgezogen;
	}

	public void setAnmeldungZurueckgezogen(boolean anmeldungZurueckgezogen) {
		this.anmeldungZurueckgezogen = anmeldungZurueckgezogen;
	}

	@Nonnull
	public JsonNode getModule() {
		return module;
	}

	public void setModule(@Nonnull JsonNode module) {
		this.module = module;
	}
}
