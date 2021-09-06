package ch.dvbern.kibon.tagesschulen.facade;

import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleBestaetigungEventDTO;
import ch.dvbern.kibon.kafka.EmitterUtil;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AnmeldungKafkaEventProducer {

	private static final Logger LOG = LoggerFactory.getLogger(AnmeldungKafkaEventProducer.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	@Channel("AnmeldungBestaetigungEvents")
	Emitter<TagesschuleBestaetigungEventDTO> anmeldungBestaetigungEvents;

	@Nonnull
	public CompletionStage<Void> process(@Nonnull TagesschuleBestaetigungEventDTO dto, @Nonnull Client client) {
		String eventType = "TagesschuleAnmeldungBestaetigung";
		String key = dto.getRefnr();

		return EmitterUtil.emitHelper(eventType, key, client, dto)
			.apply(anmeldungBestaetigungEvents, LOG);
	}
}
