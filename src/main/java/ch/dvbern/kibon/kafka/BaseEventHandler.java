package ch.dvbern.kibon.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import ch.dvbern.kibon.messagelog.MessageLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseEventHandler<T> {

	private static final Logger LOG = LoggerFactory.getLogger(BaseEventHandler.class);

	@Inject
	MessageLog log;

	public void onEvent(
		@Nonnull String key,
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull String eventType,
		@Nonnull T dto) {

		if (log.alreadyProcessed(eventId)) {
			LOG.info("Event with UUID '{}' was already retrieved, ignoring it", eventId);
			return;
		}

		LOG.info("Received '{}' event -- key: '{}', event id: '{}', event type: '{}'",
			dto.getClass().getSimpleName(), key, eventId, eventType);

		processEvent(eventId, eventTime, eventType, dto);

		log.processed(eventId);
	}

	protected abstract void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull String eventType,
		@Nonnull T dto);
}
