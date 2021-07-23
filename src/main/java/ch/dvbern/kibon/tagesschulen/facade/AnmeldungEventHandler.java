package ch.dvbern.kibon.tagesschulen.facade;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import ch.dvbern.kibon.kafka.EventType;
import ch.dvbern.kibon.tagesschulen.service.AnmeldungService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.kibon.kafka.EventType.ANMELDUNG_TAGESSCHULE;

@ApplicationScoped
public class AnmeldungEventHandler extends BaseEventHandler<TagesschuleAnmeldungEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(AnmeldungEventHandler.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	AnmeldungService anmeldungService;

	@Override
	protected void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull EventType eventType,
		@Nonnull TagesschuleAnmeldungEventDTO dto) {

		if (ANMELDUNG_TAGESSCHULE == eventType) {
			anmeldungService.onAnmeldungTagesschule(dto, eventTime);
		} else {
			LOG.warn("Unimplemented event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
