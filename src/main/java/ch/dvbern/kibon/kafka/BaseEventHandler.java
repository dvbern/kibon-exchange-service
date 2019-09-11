package ch.dvbern.kibon.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import ch.dvbern.kibon.consumedmessage.ConsumedMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseEventHandler<T> {

	private static final Logger LOG = LoggerFactory.getLogger(BaseEventHandler.class);

	@Inject
	ConsumedMessageService consumedMessageService;

	public void onEvent(
		@Nonnull String key,
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull String eventType,
		@Nonnull T dto) {

		if (consumedMessageService.alreadyProcessed(eventId)) {
			LOG.info("Event with UUID '{}' was already retrieved, ignoring it", eventId);
			return;
		}

		LOG.info("Received '{}' event -- key: '{}', event id: '{}', event type: '{}'",
			dto.getClass().getSimpleName(), key, eventId, eventType);

		try {
			processEvent(eventId, eventTime, EventType.valueOf(eventType), dto);
		} catch (IllegalArgumentException e) {
			LOG.warn("Unknown event type '{}' with id '{}'", eventType, eventId);
		}

		consumedMessageService.processed(eventId);
	}

	protected abstract void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull EventType eventType,
		@Nonnull T dto);
}
