package ch.dvbern.kibon.institution.facade;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.messagelog.MessageLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InstitutionEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(InstitutionEventHandler.class);

	@Inject
	InstitutionService institutionService;

	@Inject
	MessageLog log;

	public void onInstitutionEvent(
		@Nonnull String key,
		@Nonnull UUID eventId,
		@Nonnull String eventType,
		@Nonnull InstitutionEventDTO dto) {

		if (log.alreadyProcessed(eventId)) {
			LOG.info("Event with UUID {} was already retrieved, ignoring it", eventId);
			return;
		}

		LOG.info("Received 'Institution' event -- key: {}, event id: '{}', event type: '{}'", key, eventId, eventType);

		if (eventType.equals("InstitutionChanged")) {
			institutionService.institutionChanged(dto);
		} else {
			LOG.warn("Unknown event type {}", eventId);
		}

		log.processed(eventId);
	}
}
