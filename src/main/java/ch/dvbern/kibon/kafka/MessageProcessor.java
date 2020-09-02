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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import ch.dvbern.kibon.exchange.commons.util.DateConverter;
import ch.dvbern.kibon.exchange.commons.util.EventUtil;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

	@Transactional
	@SuppressWarnings("PMD.AvoidCatchingThrowable")
	public <T, H extends BaseEventHandler<T>> void process(
		@Nonnull KafkaRecord<String, T> message,
		@Nonnull H handler) {

		try {
			String key = message.getKey();
			Headers headers = message.getHeaders();

			Optional<String> eventIdOpt = getHeaderValue(headers, EventUtil.MESSAGE_HEADER_EVENT_ID);
			if (eventIdOpt.isEmpty()) {
				LOG.warn("Skipping Kafka message with key = {}, eventId header was missing", key);

				return;
			}

			Optional<String> eventTypeOpt = getHeaderValue(headers, EventUtil.MESSAGE_HEADER_EVENT_TYPE);
			if (eventTypeOpt.isEmpty()) {
				LOG.warn("Skipping Kafka message with key = {}, eventType header was missing", key);

				return;
			}

			UUID eventId = UUID.fromString(eventIdOpt.get());
			LocalDateTime eventTime = DateConverter.of(message.getTimestamp());

			T eventDTO = message.getPayload();

			handler.onEvent(key, eventId, eventTime, eventTypeOpt.get(), eventDTO);
		} catch (Throwable t) {
			LOG.error("Error in message processing", t);
		}
	}

	@Nonnull
	private Optional<String> getHeaderValue(@Nonnull Headers headers, @Nonnull String key) {
		return Optional.ofNullable(headers.lastHeader(key))
			.map(Header::value)
			.map(value -> new String(value, StandardCharsets.UTF_8));
	}
}
