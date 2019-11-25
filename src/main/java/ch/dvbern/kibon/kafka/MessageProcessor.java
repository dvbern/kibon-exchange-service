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
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import ch.dvbern.kibon.exchange.commons.util.EventUtil;
import ch.dvbern.kibon.util.LocalDateTimeUtil;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.kafka.MessageHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.Instant.ofEpochMilli;

@ApplicationScoped
public class MessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

	@Transactional
	@SuppressWarnings("PMD.AvoidCatchingThrowable")
	public <T, H extends BaseEventHandler<T>> void process(
		@Nonnull KafkaMessage<String, T> message,
		@Nonnull H handler) {

		try {
			String key = message.getKey();
			MessageHeaders headers = message.getHeaders();

			Optional<String> eventIdOpt = headers.getOneAsString(EventUtil.MESSAGE_HEADER_EVENT_ID);
			if (!eventIdOpt.isPresent()) {
				LOG.warn("Skipping Kafka message with key = {}, eventId header was missing", key);

				return;
			}

			Optional<String> eventTypeOpt = headers.getOneAsString(EventUtil.MESSAGE_HEADER_EVENT_TYPE);
			if (!eventTypeOpt.isPresent()) {
				LOG.warn("Skipping Kafka message with key = {}, eventType header was missing", key);

				return;
			}

			UUID eventId = UUID.fromString(eventIdOpt.get());
			LocalDateTime eventTime = LocalDateTimeUtil.of(ofEpochMilli(message.getTimestamp()));

			T eventDTO = message.getPayload();

			handler.onEvent(key, eventId, eventTime, eventTypeOpt.get(), eventDTO);
		} catch (Throwable t) {
			LOG.error("Error in message processing", t);
		}
	}
}
