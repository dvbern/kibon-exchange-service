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

package ch.dvbern.kibon.betreuung.model;

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
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

@Table(indexes = @Index(name = "betreuunganfrage_idx1", columnList = "institutionId"))
@Entity
public class BetreuungAnfrage extends AbstractInstitutionPeriodeEntity {

	@Nonnull
	@Column(nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private @NotNull BetreuungsangebotTyp betreuungsArt = BetreuungsangebotTyp.KITA;

	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode kind = null;

	@Nullable
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode gesuchsteller = null;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull Boolean abgelehntVonGesuchsteller = false;

	@Nonnull
	@Column(updatable = false)
	private @NotNull LocalDateTime eventTimestamp = LocalDateTime.now();

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BetreuungAnfrage)) {
			return false;
		}

		BetreuungAnfrage that = (BetreuungAnfrage) o;

		return super.equals(o) &&
			getBetreuungsArt() == that.getBetreuungsArt() &&
			isAbgelehntVonGesuchsteller() == that.isAbgelehntVonGesuchsteller() &&
			getEventTimestamp().equals(that.getEventTimestamp());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			Arrays.hashCode(baseHashCodeValues()),
			getBetreuungsArt(),
			isAbgelehntVonGesuchsteller(),
			getEventTimestamp());
	}

	@Nonnull
	public BetreuungsangebotTyp getBetreuungsArt() {
		return betreuungsArt;
	}

	public void setBetreuungsArt(@Nonnull BetreuungsangebotTyp betreuungsArt) {
		this.betreuungsArt = betreuungsArt;
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

	public boolean isAbgelehntVonGesuchsteller() {
		return abgelehntVonGesuchsteller;
	}

	public void setAbgelehntVonGesuchsteller(boolean abgelehntVonGesuchsteller) {
		this.abgelehntVonGesuchsteller = abgelehntVonGesuchsteller;
	}

	@Nonnull
	public LocalDateTime getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(@Nonnull LocalDateTime eventTime) {
		this.eventTimestamp = eventTime;
	}
}
