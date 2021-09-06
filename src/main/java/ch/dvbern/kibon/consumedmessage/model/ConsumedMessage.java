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

package ch.dvbern.kibon.consumedmessage.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.persistence.BaseEntity;

/**
 * Used to keep track of received events.
 */
@Table(indexes = @Index(name = "consumedmessage_idx1", columnList = "eventId, timeOfReceiving"))
@Entity
public class ConsumedMessage extends BaseEntity {

	@Nonnull
	@Id
	@Column(nullable = false, unique = true)
	private @NotNull UUID eventId;

	@Nonnull
	@Column(nullable = false)
	private @NotNull Instant timeOfReceiving;

	public ConsumedMessage() {
		eventId = UUID.randomUUID();
		timeOfReceiving = Instant.now();
	}

	public ConsumedMessage(@Nonnull UUID eventId, @Nonnull Instant timeOfReceiving) {
		this.eventId = eventId;
		this.timeOfReceiving = timeOfReceiving;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof ConsumedMessage)) {
			return false;
		}

		ConsumedMessage that = (ConsumedMessage) o;

		return getEventId().equals(that.getEventId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEventId());
	}

	@Nonnull
	public UUID getEventId() {
		return eventId;
	}

	public void setEventId(@Nonnull UUID eventId) {
		this.eventId = eventId;
	}

	@Nonnull
	public Instant getTimeOfReceiving() {
		return timeOfReceiving;
	}

	public void setTimeOfReceiving(@Nonnull Instant timeOfReceiving) {
		this.timeOfReceiving = timeOfReceiving;
	}
}
