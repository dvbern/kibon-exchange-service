package ch.dvbern.kibon.verfuegung.facade;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import ch.dvbern.kibon.exchange.commons.util.ObjectMapperUtil;
import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.kafka.MessageHeaders;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VerfuegungKafkaEventConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(VerfuegungKafkaEventConsumer.class);

	@Inject
	VerfuegungEventHandler verfuegungEventHandler;

	@Transactional
	@Incoming("VerfuegungEvents")
	public CompletionStage<Void> onMessage(@Nonnull KafkaMessage<String, byte[]> message) {
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
			VerfuegungEventDTO verfuegungEventDTO =
				ObjectMapperUtil.MAPPER.readValue(message.getPayload(), VerfuegungEventDTO.class);

			verfuegungEventHandler.onVerfuegungEvent(key, eventId, eventTypeOpt.get(), verfuegungEventDTO);
		} catch (Throwable t) {
			LOG.error("Error in message processing", t);
		}

		return message.ack();
	}
}
