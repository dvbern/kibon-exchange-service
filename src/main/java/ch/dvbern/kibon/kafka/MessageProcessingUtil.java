package ch.dvbern.kibon.kafka;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.util.ObjectMapperUtil;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.kafka.MessageHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessageProcessingUtil {

	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessingUtil.class);

	private MessageProcessingUtil() {
		// util
	}

	@Nonnull
	public static <T> CompletionStage<Void> process(
		@Nonnull KafkaMessage<String, byte[]> message,
		@Nonnull Class<T> payloadClass,
		@Nonnull MessageHandler<T> handler) {

		try {
			String key = message.getKey();
			MessageHeaders headers = message.getHeaders();

			Optional<String> eventIdOpt = headers.getOneAsString(ObjectMapperUtil.MESSAGE_HEADER_EVENT_ID);
			if (!eventIdOpt.isPresent()) {
				LOG.warn("Skipping Kafka messasge with key = {}, eventId header was missing", key);

				return message.ack();
			}

			Optional<String> eventTypeOpt = headers.getOneAsString(ObjectMapperUtil.MESSAGE_HEADER_EVENT_TYPE);
			if (!eventTypeOpt.isPresent()) {
				LOG.warn("Skipping Kafka messasge with key = {}, eventType header was missing", key);

				return message.ack();
			}

			UUID eventId = UUID.fromString(eventIdOpt.get());

			T eventDTO = ObjectMapperUtil.MAPPER.readValue(message.getPayload(), payloadClass);

			handler.handle(key, eventId, eventTypeOpt.get(), eventDTO);
		} catch (Throwable t) {
			LOG.error("Error in message processing", t);
		}

		return message.ack();
	}
}
