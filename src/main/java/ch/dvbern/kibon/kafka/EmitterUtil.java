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

package ch.dvbern.kibon.kafka;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.clients.model.Client;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;

import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_CLIENT_NAME;
import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_EVENT_ID;
import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_EVENT_TYPE;

public final class EmitterUtil {

	private EmitterUtil() {
	}

	@Nonnull
	public static <T> BiFunction<Emitter<T>, Logger, CompletionStage<Void>> emitHelper(
		@Nonnull String eventType,
		@Nonnull String key,
		@Nonnull Client client,
		@Nonnull T payload) {

		return (emitter, logger) -> {

			OutgoingKafkaRecordMetadata<String> metadata = buildMetadata(eventType, key, client);

			// there are two different send methods: the one that accepts a payload returns a CompletionStage, which
			// will
			// be completed when the message for this payload is acknowledged. Unfortunately, the one that accepts a
			// message does not return anything. We thus provide our our own ack & nack functions, similar as
			// in the payload-parameter method of EmitterImpl, to block the REST request until an ACK is received.
			CompletableFuture<Void> future = new CompletableFuture<>();

			Message<T> message = KafkaRecord.of(key, payload)
				.addMetadata(metadata)
				.withAck(() -> {
					logger.info("ACK {} / {}", eventType, key);
					future.complete(null);
					return CompletableFuture.completedFuture(null);
				})
				.withNack(reason -> {
					logger.info("NACK {} / {}", eventType, key);
					future.completeExceptionally(reason);
					return CompletableFuture.completedFuture(null);
				});

			try {
				emitter.send(message);
			} catch (Exception e) {
				logger.error("Emitter failed sending {}, with refNr. {} to Kafka", eventType, key, e);
				future.completeExceptionally(e);
			}

			return future;
		};
	}

	@Nonnull
	private static OutgoingKafkaRecordMetadata<String> buildMetadata(
		@Nonnull String eventType,
		@Nonnull String key,
		@Nonnull Client client) {

		OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(key)
			.withHeaders(new RecordHeaders()
				.add(MESSAGE_HEADER_EVENT_ID, UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
				.add(MESSAGE_HEADER_EVENT_TYPE, eventType.getBytes(StandardCharsets.UTF_8))
				.add(MESSAGE_HEADER_CLIENT_NAME, client.getId().getClientName().getBytes(StandardCharsets.UTF_8))
			)
			.build();

		return metadata;
	}
}
