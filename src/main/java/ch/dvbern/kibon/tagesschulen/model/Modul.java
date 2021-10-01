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

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.persistence.BaseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import org.hibernate.annotations.Type;

@Table(indexes = @Index(name = "modul_idx1", columnList = "parent_id"))
@Entity
public class Modul extends BaseEntity {

	@Id
	@Nonnull
	private @NotEmpty String id = "";

	@Nonnull
	// allowing nullable DB schema, to make hibernate cascading from institution -> parent -> module work,
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "tagesschule_module_fk"), nullable = false, updatable = false)
	private @NotNull TagesschuleModule parent = new TagesschuleModule();

	@Nonnull
	private @NotNull String bezeichnungDE = "";

	@Nonnull
	private @NotNull String bezeichnungFR = "";

	@Nonnull
	private @NotNull LocalTime zeitVon = LocalTime.MIN;

	@Nonnull
	private @NotNull LocalTime zeitBis = LocalTime.MAX;

	/**
	 * A {@link java.util.List<ch.dvbern.kibon.exchange.commons.types.Wochentag>}
	 */
	@SuppressWarnings("UnnecessaryFullyQualifiedName")
	@Nonnull
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN)
	private @NotNull JsonNode wochentage;

	/**
	 * A {@link java.util.List<ch.dvbern.kibon.exchange.commons.types.Intervall>}
	 */
	@SuppressWarnings("UnnecessaryFullyQualifiedName")
	@Nonnull
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN)
	private @NotNull JsonNode erlaubteIntervalle;

	private boolean wirdPaedagogischBetreut;

	@Nonnull
	private @NotNull BigDecimal verpflegungsKosten;

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Modul)) {
			return false;
		}

		Modul that = (Modul) o;

		return !getId().isEmpty()
			&& getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Nonnull
	public String getId() {
		return id;
	}

	public void setId(@Nonnull String id) {
		this.id = id;
	}

	@Nonnull
	public TagesschuleModule getParent() {
		return parent;
	}

	public void setParent(@Nonnull TagesschuleModule parent) {
		this.parent = parent;
	}

	@Nonnull
	public String getBezeichnungDE() {
		return bezeichnungDE;
	}

	public void setBezeichnungDE(@Nonnull String bezeichnungDE) {
		this.bezeichnungDE = bezeichnungDE;
	}

	@Nonnull
	public String getBezeichnungFR() {
		return bezeichnungFR;
	}

	public void setBezeichnungFR(@Nonnull String bezeichnungFR) {
		this.bezeichnungFR = bezeichnungFR;
	}

	@Nonnull
	public LocalTime getZeitVon() {
		return zeitVon;
	}

	public void setZeitVon(@Nonnull LocalTime zeitVon) {
		this.zeitVon = zeitVon;
	}

	@Nonnull
	public LocalTime getZeitBis() {
		return zeitBis;
	}

	public void setZeitBis(@Nonnull LocalTime zeitBis) {
		this.zeitBis = zeitBis;
	}

	@Nonnull
	public JsonNode getWochentage() {
		return wochentage;
	}

	public void setWochentage(@Nonnull JsonNode wochentage) {
		this.wochentage = wochentage;
	}

	@Nonnull
	public JsonNode getErlaubteIntervalle() {
		return erlaubteIntervalle;
	}

	public void setErlaubteIntervalle(@Nonnull JsonNode erlaubteIntervalle) {
		this.erlaubteIntervalle = erlaubteIntervalle;
	}

	public boolean isWirdPaedagogischBetreut() {
		return wirdPaedagogischBetreut;
	}

	public void setWirdPaedagogischBetreut(boolean padaegogischBetreut) {
		this.wirdPaedagogischBetreut = padaegogischBetreut;
	}

	@Nonnull
	public BigDecimal getVerpflegungsKosten() {
		return verpflegungsKosten;
	}

	public void setVerpflegungsKosten(@Nonnull BigDecimal verpflegungsKosten) {
		this.verpflegungsKosten = verpflegungsKosten;
	}
}
