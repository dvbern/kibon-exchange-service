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

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.exchange.commons.types.Regelwerk;
import com.fasterxml.jackson.databind.JsonNode;

public class ClientVerfuegungDTO {

	@Nonnull
	private final Long id;

	@Nonnull
	private final LocalDateTime availableSince;

	@Nonnull
	private final String refnr;

	@Nonnull
	private final String institutionId;

	@Nonnull
	private final LocalDate von;

	@Nonnull
	private final LocalDate bis;

	@Nonnull
	private final Integer version;

	@Nonnull
	private final LocalDateTime verfuegtAm;

	@Nonnull
	private final BetreuungsangebotTyp betreuungsArt;

	@Nonnull
	private final Long gemeindeBfsNr;

	@Nonnull
	private final String gemeindeName;

	@Nonnull
	private final JsonNode kind;

	@Nonnull
	private final JsonNode gesuchsteller;

	@Nonnull
	private final JsonNode zeitabschnitte;

	@Nonnull
	private final JsonNode ignorierteZeitabschnitte;

	@Nonnull
	private Regelwerk regelwerk = Regelwerk.ASIV;

	public ClientVerfuegungDTO(
		@Nonnull Long id,
		@Nonnull LocalDateTime availableSince,
		@Nonnull String refnr,
		@Nonnull String institutionId,
		@Nonnull LocalDate von,
		@Nonnull LocalDate bis,
		@Nonnull Integer version,
		@Nonnull LocalDateTime verfuegtAm,
		@Nonnull BetreuungsangebotTyp betreuungsArt,
		@Nonnull Long gemeindeBfsNr,
		@Nonnull String gemeindeName,
		@Nonnull JsonNode kind,
		@Nonnull JsonNode gesuchsteller,
		@Nonnull JsonNode zeitabschnitte,
		@Nonnull JsonNode ignorierteZeitabschnitte,
		@Nonnull Regelwerk regelwerk
	) {
		this.id = id;
		this.availableSince = availableSince;
		this.refnr = refnr;
		this.institutionId = institutionId;
		this.von = von;
		this.bis = bis;
		this.version = version;
		this.verfuegtAm = verfuegtAm;
		this.betreuungsArt = betreuungsArt;
		this.gemeindeBfsNr = gemeindeBfsNr;
		this.gemeindeName = gemeindeName;
		this.kind = kind;
		this.gesuchsteller = gesuchsteller;
		this.zeitabschnitte = zeitabschnitte;
		this.ignorierteZeitabschnitte = ignorierteZeitabschnitte;
		this.regelwerk = regelwerk;
	}

	@Nonnull
	public Long getId() {
		return id;
	}

	@Nonnull
	public LocalDateTime getAvailableSince() {
		return availableSince;
	}

	@Nonnull
	public String getRefnr() {
		return refnr;
	}

	@Nonnull
	public String getInstitutionId() {
		return institutionId;
	}

	@Nonnull
	public LocalDate getVon() {
		return von;
	}

	@Nonnull
	public LocalDate getBis() {
		return bis;
	}

	@Nonnull
	public Integer getVersion() {
		return version;
	}

	@Nonnull
	public LocalDateTime getVerfuegtAm() {
		return verfuegtAm;
	}

	@Nonnull
	public BetreuungsangebotTyp getBetreuungsArt() {
		return betreuungsArt;
	}

	@Nonnull
	public Long getGemeindeBfsNr() {
		return gemeindeBfsNr;
	}

	@Nonnull
	public String getGemeindeName() {
		return gemeindeName;
	}

	@Nonnull
	public JsonNode getKind() {
		return kind;
	}

	@Nonnull
	public JsonNode getGesuchsteller() {
		return gesuchsteller;
	}

	@Nonnull
	public JsonNode getZeitabschnitte() {
		return zeitabschnitte;
	}

	@Nonnull
	public JsonNode getIgnorierteZeitabschnitte() {
		return ignorierteZeitabschnitte;
	}

	@Nonnull
	public Regelwerk getRegelwerk() {
		return regelwerk;
	}
}
