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
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
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

			// sometimes metadata is lost! While debugging, I noticed there are 2 different
			// OutgoingKafkaRecordMetadata classes used. One didn't contain metadata the other one did. Not really
			// sure if this will help at all, but might be better to have the same metadata in both cases.
			var headers = createRecordHeaders(eventType, client);
			var metadata = buildMetadata(key, headers);
			var metadataDep = buildMetadataDeprecated(key, headers);

			// there are two different send methods: the one that accepts a payload returns a CompletionStage, which
			// will be completed when the message for this payload is acknowledged. Unfortunately, the one that
			// accepts a message does not return anything. We thus provide our our own ack & nack functions, similar as
			// in the payload-parameter method of EmitterImpl, to block the REST request until an ACK is received.
			CompletableFuture<Void> future = new CompletableFuture<>();

			Message<T> message = KafkaRecord.of(key, payload)
				.addMetadata(Metadata.of(metadata, metadataDep))
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
	private static Headers createRecordHeaders(@Nonnull String eventType, @Nonnull Client client) {
		return new RecordHeaders()
			.add(MESSAGE_HEADER_EVENT_ID, UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
			.add(MESSAGE_HEADER_EVENT_TYPE, eventType.getBytes(StandardCharsets.UTF_8))
			.add(MESSAGE_HEADER_CLIENT_NAME, client.getId().getClientName().getBytes(StandardCharsets.UTF_8));
	}

	@Nonnull
	private static OutgoingKafkaRecordMetadata<String> buildMetadata(
		@Nonnull String key,
		@Nonnull Headers headers) {

		OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(key)
			.withHeaders(headers)
			.build();

		return metadata;
	}

	@Nonnull
	private static io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata<String> buildMetadataDeprecated(
		@Nonnull String key,
		@Nonnull Headers headers) {

		io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata<String> metadata =
			io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata.<String>builder()
				.withKey(key)
				.withHeaders(headers)
				.build();

		return metadata;
	}
}
