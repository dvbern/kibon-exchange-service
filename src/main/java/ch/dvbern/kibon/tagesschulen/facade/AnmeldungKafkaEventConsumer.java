package ch.dvbern.kibon.tagesschulen.facade;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.kafka.IncomingEvent;
import ch.dvbern.kibon.kafka.MessageProcessor;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class AnmeldungKafkaEventConsumer {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	AnmeldungEventHandler eventHandler;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	MessageProcessor processor;

	@Incoming("AnmeldungEvents")
	@Outgoing("AnmeldungEvents-internal")
	@Nullable
	public Message<IncomingEvent<TagesschuleAnmeldungEventDTO>> wrap(
		@Nonnull IncomingKafkaRecord<String, TagesschuleAnmeldungEventDTO> msg) {

		return processor.toIncomingEvent(msg);
	}

	@Incoming("AnmeldungEvents-internal")
	@Blocking
	public void onMessage(@Nonnull IncomingEvent<TagesschuleAnmeldungEventDTO> event) {
		processor.process(event, eventHandler);
	}
}
