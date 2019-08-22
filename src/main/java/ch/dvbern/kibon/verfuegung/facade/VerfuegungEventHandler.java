package ch.dvbern.kibon.verfuegung.facade;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.messagelog.MessageLog;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VerfuegungEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(VerfuegungEventHandler.class);

	@Inject
	VerfuegungService verfuegungService;

	@Inject
	MessageLog log;

	public void onVerfuegungEvent(
		@Nonnull String key,
		@Nonnull UUID eventId,
		@Nonnull String eventType,
		@Nonnull VerfuegungEventDTO dto) {

		if (log.alreadyProcessed(eventId)) {
			LOG.info("Event with UUID {} was already retrieved, ignoring it", eventId);
			return;
		}

		LOG.info("Received 'Verfuegung' event -- key: {}, event id: '{}', event type: '{}'", key, eventId, eventType);

		if (eventType.equals("VerfuegungVerfuegt")) {
			verfuegungService.verfuegungCreated(dto);
		} else {
			LOG.warn("Unknown event type {}", eventId);
		}

		log.processed(eventId);
	}
}
