package ch.dvbern.kibon.kafka;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.util.ObjectMapperUtil;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.kafka.MessageHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneId.systemDefault;

public final class MessageProcessingUtil {

	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessingUtil.class);

	private MessageProcessingUtil() {
		// util
	}

	@Nonnull
	public static <T, H extends BaseEventHandler<T>> CompletionStage<Void> process(
		@Nonnull KafkaMessage<String, byte[]> message,
		@Nonnull Class<T> payloadClass,
		@Nonnull H handler) {

		try {
			String key = message.getKey();
			MessageHeaders headers = message.getHeaders();

			Optional<String> eventIdOpt = headers.getOneAsString(ObjectMapperUtil.MESSAGE_HEADER_EVENT_ID);
			if (!eventIdOpt.isPresent()) {
				LOG.warn("Skipping Kafka message with key = {}, eventId header was missing", key);

				return message.ack();
			}

			Optional<String> eventTypeOpt = headers.getOneAsString(ObjectMapperUtil.MESSAGE_HEADER_EVENT_TYPE);
			if (!eventTypeOpt.isPresent()) {
				LOG.warn("Skipping Kafka message with key = {}, eventType header was missing", key);

				return message.ack();
			}

			UUID eventId = UUID.fromString(eventIdOpt.get());
			LocalDateTime eventTime = LocalDateTime.ofInstant(ofEpochMilli(message.getTimestamp()), systemDefault());

			T eventDTO = ObjectMapperUtil.MAPPER.readValue(message.getPayload(), payloadClass);

			handler.onEvent(key, eventId, eventTime, eventTypeOpt.get(), eventDTO);
		} catch (Throwable t) {
			LOG.error("Error in message processing", t);
		}

		return message.ack();
	}
}
