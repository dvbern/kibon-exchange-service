package ch.dvbern.kibon.clients.facade;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ClientEventHandler extends BaseEventHandler<InstitutionClientEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(ClientEventHandler.class);

	@Inject
	ClientService clientService;

	@Override
	protected void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull String eventType,
		@Nonnull InstitutionClientEventDTO dto) {

		if (!dto.getClientType().equals("EXCHANGE_SERVICE_USER")) {
			LOG.warn("Unknown clientType '{}' for event type '{}' with id '{}'",
				dto.getClientType(), eventType, eventId);

			return;
		}

		if (eventType.equals("ClientAdded")) {
			clientService.clientAdded(dto, eventTime);
		} else if (eventType.equals("ClientRemoved")) {
			clientService.clientRemoved(dto);
		} else {
			LOG.warn("Unknown event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
