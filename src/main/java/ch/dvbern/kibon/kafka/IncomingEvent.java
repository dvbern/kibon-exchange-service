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

package ch.dvbern.kibon.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;

public class IncomingEvent<T> {

	@Nonnull
	private final String key;
	@Nonnull
	private final UUID eventId;
	@Nonnull
	private final LocalDateTime eventTime;
	@Nonnull
	private final String eventType;
	@Nonnull
	private final T payload;

	public IncomingEvent(
		@Nonnull String key,
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull String eventType,
		@Nonnull T payload) {
		this.key = key;
		this.eventId = eventId;
		this.eventTime = eventTime;
		this.eventType = eventType;
		this.payload = payload;
	}

	@Nonnull
	public String getKey() {
		return key;
	}

	@Nonnull
	public UUID getEventId() {
		return eventId;
	}

	@Nonnull
	public LocalDateTime getEventTime() {
		return eventTime;
	}

	@Nonnull
	public String getEventType() {
		return eventType;
	}

	@Nonnull
	public T getPayload() {
		return payload;
	}
}
