package ch.dvbern.kibon.clients.facade;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import ch.dvbern.kibon.kafka.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.kibon.kafka.EventType.CLIENT_ADDED;
import static ch.dvbern.kibon.kafka.EventType.CLIENT_REMOVED;

@ApplicationScoped
public class ClientEventHandler extends BaseEventHandler<InstitutionClientEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(ClientEventHandler.class);

	@Inject
	ClientService clientService;

	@Override
	protected void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull EventType eventType,
		@Nonnull InstitutionClientEventDTO dto) {

		if (!dto.getClientType().equals("EXCHANGE_SERVICE_USER")) {
			LOG.warn("Unknown clientType '{}' for event type '{}' with id '{}'",
				dto.getClientType(), eventType, eventId);

			return;
		}

		if (CLIENT_ADDED == eventType) {
			clientService.onClientAdded(dto, eventTime);
		} else if (CLIENT_REMOVED == eventType) {
			clientService.onClientRemoved(dto);
		} else {
			LOG.warn("Unimplemented event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
