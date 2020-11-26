package ch.dvbern.kibon.betreuung.facade;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.betreuung.model.BetreuungStornierungAnfrage;
import ch.dvbern.kibon.clients.model.Client;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_CLIENT_NAME;
import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_EVENT_ID;
import static ch.dvbern.kibon.exchange.commons.util.EventUtil.MESSAGE_HEADER_EVENT_TYPE;

@ApplicationScoped
public class BetreuungStornierungAnfrageKafkaEventProducer {

	private static final Logger LOG = LoggerFactory.getLogger(BetreuungStornierungAnfrageKafkaEventProducer.class);

	@Inject
	@Channel("BetreuungStornierungEvents")
	Emitter<String> betreuungStornierungEvent;

	@Nonnull
	public CompletionStage<Void> process(@Nonnull BetreuungStornierungAnfrage betreuungStornierungAnfrage, @Nonnull Client client) {
		String eventType = "BetreuungStornierungAnfrage";
		String key = betreuungStornierungAnfrage.getRefnr();

		OutgoingKafkaRecordMetadata metadata = OutgoingKafkaRecordMetadata.builder()
			.withKey(key)
			.withHeaders(new RecordHeaders()
				.add(MESSAGE_HEADER_EVENT_ID, UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
				.add(MESSAGE_HEADER_EVENT_TYPE, eventType.getBytes(StandardCharsets.UTF_8))
				.add(MESSAGE_HEADER_CLIENT_NAME, client.getId().getClientName().getBytes(StandardCharsets.UTF_8))
				.add(
					"timestamp",
					Long.toString(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8)))
			.build();

		// there are two different send methods: the one that accepts a payload returns a CompletionStage, which will
		// be completed when the message for this payload is acknowledged. Unfortunately, the one that accepts a
		// message does not return anything. We thus provide our our own ack & nack functions, similar as
		// in the payload-parameter method of EmitterImpl, to block the REST request until an ACK is received.
		CompletableFuture<Void> future = new CompletableFuture<>();

		Message<String> message = KafkaRecord.of(key, key)
			.addMetadata(metadata)
			.withAck(() -> {
				LOG.info("ACK BetreuungStornierungEvent");
				future.complete(null);
				return CompletableFuture.completedFuture(null);
			})
			.withNack((reason) -> {
				LOG.info("NACK BetruungStornierungEvent");
				future.completeExceptionally(reason);
				return CompletableFuture.completedFuture(null);
			});

		try {
			betreuungStornierungEvent.send(message);
		} catch (Exception e) {
			LOG.error("Emitter failed sending BetreuungStornierung with refNr. {} to Kafka", key, e);
			future.completeExceptionally(e);
		}

		return future;
	}
}
