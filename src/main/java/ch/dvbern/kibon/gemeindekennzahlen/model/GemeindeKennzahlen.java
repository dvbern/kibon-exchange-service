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

package ch.dvbern.kibon.gemeindekennzahlen.model;

import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.types.EinschulungTyp;
import ch.dvbern.kibon.exchange.commons.types.Mandant;
import ch.dvbern.kibon.persistence.BaseEntity;
import ch.dvbern.kibon.util.ConstantsUtil;

@Table
@Entity
public class GemeindeKennzahlen extends BaseEntity {

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@Column(nullable = false, updatable = true)
	private Long bfsNummer;

	@Nonnull
	@Column(nullable = false, updatable = true)
	private LocalDate gesuchsperiodeStart;

	@Nonnull
	@Column(nullable = false, updatable = true)
	private LocalDate gesuchsperiodeStop;

	@Nullable
	@Column(nullable = true, updatable = true)
	private Boolean kontingentierung;

	@Nullable
	@Column(nullable = true, updatable = true)
	private Boolean kontingentierungAusgeschoepft;

	@Nullable
	@Column(nullable = true, updatable = true)
	private BigDecimal anzahlKinderWarteliste = null;

	@Nullable
	@Column(nullable = true, updatable = true)
	private BigDecimal dauerWarteliste = null;

	@Nullable
	@Column(nullable = true, updatable = true)
	private BigDecimal erwerbspensumZuschlag = null;

	@Nullable
	@Enumerated(EnumType.STRING)
	@Column(length = ConstantsUtil.SHORT_COLUMN_SIZE)
	private EinschulungTyp limitierungTfo;

	@Nullable
	@Enumerated(EnumType.STRING)
	@Column(length = ConstantsUtil.SHORT_COLUMN_SIZE)
	private EinschulungTyp limitierungKita;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Mandant mandant = Mandant.BERN;

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof GemeindeKennzahlen)) {
			return false;
		}

		GemeindeKennzahlen that = (GemeindeKennzahlen) o;

		return getId().equals(that.getId()) &&
			getBfsNummer().equals(that.getBfsNummer()) &&
			getGesuchsperiodeStart().equals(that.getGesuchsperiodeStart()) &&
			getGesuchsperiodeStop().equals(that.getGesuchsperiodeStop());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getBfsNummer(), getGesuchsperiodeStart(), getGesuchsperiodeStop());
	}

	@Nonnull
	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Nonnull
	public Long getBfsNummer() {
		return bfsNummer;
	}

	public void setBfsNummer(@Nonnull Long bfsNummer) {
		this.bfsNummer = bfsNummer;
	}

	@Nonnull
	public LocalDate getGesuchsperiodeStart() {
		return gesuchsperiodeStart;
	}

	public void setGesuchsperiodeStart(@Nonnull LocalDate gesuchsperiodeStart) {
		this.gesuchsperiodeStart = gesuchsperiodeStart;
	}

	@Nonnull
	public LocalDate getGesuchsperiodeStop() {
		return gesuchsperiodeStop;
	}

	public void setGesuchsperiodeStop(@Nonnull LocalDate gesuchsperiodeStop) {
		this.gesuchsperiodeStop = gesuchsperiodeStop;
	}

	@Nullable
	public Boolean getKontingentierung() {
		return kontingentierung;
	}

	public void setKontingentierung(@Nullable Boolean kontingentierung) {
		this.kontingentierung = kontingentierung;
	}

	@Nullable
	public Boolean getKontingentierungAusgeschoepft() {
		return kontingentierungAusgeschoepft;
	}

	public void setKontingentierungAusgeschoepft(@Nullable Boolean kontingentierungAusgeschoepft) {
		this.kontingentierungAusgeschoepft = kontingentierungAusgeschoepft;
	}

	@Nullable
	public BigDecimal getAnzahlKinderWarteliste() {
		return anzahlKinderWarteliste;
	}

	public void setAnzahlKinderWarteliste(@Nullable BigDecimal anzahlKinderWarteliste) {
		this.anzahlKinderWarteliste = anzahlKinderWarteliste;
	}

	@Nullable
	public BigDecimal getDauerWarteliste() {
		return dauerWarteliste;
	}

	public void setDauerWarteliste(@Nullable BigDecimal dauerWarteliste) {
		this.dauerWarteliste = dauerWarteliste;
	}

	@Nullable
	public BigDecimal getErwerbspensumZuschlag() {
		return erwerbspensumZuschlag;
	}

	public void setErwerbspensumZuschlag(@Nullable BigDecimal erwerbspensumZuschlag) {
		this.erwerbspensumZuschlag = erwerbspensumZuschlag;
	}

	@Nullable
	public EinschulungTyp getLimitierungTfo() {
		return limitierungTfo;
	}

	public void setLimitierungTfo(@Nullable EinschulungTyp limitierungTfo) {
		this.limitierungTfo = limitierungTfo;
	}

	@Nullable
	public EinschulungTyp getLimitierungKita() {
		return limitierungKita;
	}

	public void setLimitierungKita(@Nullable EinschulungTyp limitierungKita) {
		this.limitierungKita = limitierungKita;
	}

	public Mandant getMandant() {
		return mandant;
	}

	public void setMandant(Mandant mandant) {
		this.mandant = mandant;
	}
}
