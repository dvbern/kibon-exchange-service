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

package ch.dvbern.kibon.platzbestaetigung.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import com.fasterxml.jackson.databind.JsonNode;

public class ClientBetreuungAnfrageDTO {

	@Nonnull
	private final Long id;

	@Nonnull
	private final String refnr;

	@Nonnull
	private final String institutionId;

	@Nonnull
	private final LocalDate periodeVon;

	@Nonnull
	private final LocalDate periodeBis;

	@Nonnull
	private final BetreuungsangebotTyp betreuungsArt;

	@Nonnull
	private final JsonNode kind;

	@Nonnull
	private final JsonNode gesuchsteller;

	@Nonnull
	private final Boolean abgelehntVonGesuchsteller;

	@Nonnull
	private final LocalDateTime eventTimestamp;

	public ClientBetreuungAnfrageDTO(
		@Nonnull Long id,
		@Nonnull String refnr,
		@Nonnull String institutionId,
		@Nonnull LocalDate periodeVon,
		@Nonnull LocalDate periodeBis,
		@Nonnull BetreuungsangebotTyp betreuungsArt,
		@Nonnull JsonNode kind,
		@Nonnull JsonNode gesuchsteller,
		@Nonnull Boolean abgelehntVonGesuchsteller,
		@Nonnull LocalDateTime eventTimestamp) {
		this.id = id;
		this.refnr = refnr;
		this.institutionId = institutionId;
		this.periodeVon = periodeVon;
		this.periodeBis = periodeBis;
		this.betreuungsArt = betreuungsArt;
		this.kind = kind;
		this.gesuchsteller = gesuchsteller;
		this.abgelehntVonGesuchsteller = abgelehntVonGesuchsteller;
		this.eventTimestamp = eventTimestamp;
	}

	@Nonnull
	public Long getId() {
		return id;
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
	public LocalDate getPeriodeVon() {
		return periodeVon;
	}

	@Nonnull
	public LocalDate getPeriodeBis() {
		return periodeBis;
	}

	@Nonnull
	public BetreuungsangebotTyp getBetreuungsArt() {
		return betreuungsArt;
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
	public Boolean getAbgelehntVonGesuchsteller() {
		return abgelehntVonGesuchsteller;
	}

	@Nonnull
	public LocalDateTime getEventTimestamp() {
		return eventTimestamp;
	}
}
