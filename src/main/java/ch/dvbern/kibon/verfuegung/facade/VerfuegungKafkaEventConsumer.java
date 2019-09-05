package ch.dvbern.kibon.verfuegung.facade;

import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.kafka.MessageProcessingUtil;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class VerfuegungKafkaEventConsumer {

	@Inject
	VerfuegungEventHandler verfuegungEventHandler;

	@Transactional
	@Incoming("VerfuegungEvents")
	public CompletionStage<Void> onMessage(@Nonnull KafkaMessage<String, byte[]> message) {

		return MessageProcessingUtil.process(message, VerfuegungEventDTO.class, verfuegungEventHandler);
	}
}
