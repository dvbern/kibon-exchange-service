package ch.dvbern.kibon.verfuegung.facade;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VerfuegungEventHandler extends BaseEventHandler<VerfuegungEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(VerfuegungEventHandler.class);

	@Inject
	VerfuegungService verfuegungService;

	@Override
	protected void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull String eventType,
		@Nonnull VerfuegungEventDTO dto) {

		if (eventType.equals("VerfuegungVerfuegt")) {
			verfuegungService.verfuegungCreated(dto);
		} else {
			LOG.warn("Unknown event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
