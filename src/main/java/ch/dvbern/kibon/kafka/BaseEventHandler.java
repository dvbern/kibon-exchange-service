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

package ch.dvbern.kibon.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import ch.dvbern.kibon.consumedmessage.ConsumedMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseEventHandler<T> {

	private static final Logger LOG = LoggerFactory.getLogger(BaseEventHandler.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ConsumedMessageService consumedMessageService;

	public void onEvent(
		@Nonnull String key,
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull String eventType,
		@Nonnull T dto) {

		if (consumedMessageService.alreadyProcessed(eventId)) {
			LOG.info("Event with UUID '{}' was already retrieved, ignoring it", eventId);
			return;
		}

		LOG.info("Received '{}' event -- key: '{}', event id: '{}', event type: '{}'",
			dto.getClass().getSimpleName(), key, eventId, eventType);

		try {
			processEvent(eventId, eventTime, EventType.of(eventType), dto);
		} catch (IllegalArgumentException e) {
			LOG.warn("Unknown event type '{}' with id '{}'", eventType, eventId);
		}

		consumedMessageService.processed(eventId);
	}

	protected abstract void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull EventType eventType,
		@Nonnull T dto);
}
