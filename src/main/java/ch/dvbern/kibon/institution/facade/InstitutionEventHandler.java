package ch.dvbern.kibon.institution.facade;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import ch.dvbern.kibon.kafka.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.kibon.kafka.EventType.INSTITUTION_CHANGED;

@ApplicationScoped
public class InstitutionEventHandler extends BaseEventHandler<InstitutionEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(InstitutionEventHandler.class);

	@Inject
	InstitutionService institutionService;

	@Override
	protected void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull EventType eventType,
		@Nonnull InstitutionEventDTO dto) {

		if (INSTITUTION_CHANGED == eventType) {
			institutionService.onInstitutionChanged(dto);
		} else {
			LOG.warn("Unimplemented event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
