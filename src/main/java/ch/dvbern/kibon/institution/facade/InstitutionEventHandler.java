package ch.dvbern.kibon.institution.facade;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InstitutionEventHandler extends BaseEventHandler<InstitutionEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(InstitutionEventHandler.class);

	@Inject
	InstitutionService institutionService;

	@Override
	protected void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull String eventType,
		@Nonnull InstitutionEventDTO dto) {

		if (eventType.equals("InstitutionChanged")) {
			institutionService.institutionChanged(dto);
		} else {
			LOG.warn("Unknown event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
