package ch.dvbern.kibon.clients.facade;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import ch.dvbern.kibon.testutils.EventHandlerTest;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
class ClientEventHandlerTest extends EventHandlerTest<InstitutionClientEventDTO> {

	@TestSubject
	private final ClientEventHandler handler = new ClientEventHandler();

	@SuppressWarnings({ "unused", "InstanceVariableMayNotBeInitialized" })
	@Mock(type = MockType.STRICT)
	private ClientService clientService;

	@Test
	public void testHandleClientAddedEvent() {
		InstitutionClientEventDTO dto = expectedDTO();

		clientService.clientAdded(eq(dto), anyObject());
		expectLastCall();

		expectEventProcessing("ClientAdded", dto);
	}

	@Test
	public void testHandleClientRemovedEvent() {
		InstitutionClientEventDTO dto = expectedDTO();

		clientService.clientRemoved(dto);
		expectLastCall();

		expectEventProcessing("ClientRemoved", dto);
	}

	@Test
	public void testProcessUnknownEvent() {
		expectEventProcessing("unknown", expectedDTO());
	}

	@Test
	public void testIgnoreUnknownClientType() {
		InstitutionClientEventDTO dto = new InstitutionClientEventDTO();
		dto.setClientType("NON_EXCHANGE_SERVICE_USER");

		expectEventProcessing("ClientAdded", dto);
	}

	@Nonnull
	private InstitutionClientEventDTO expectedDTO() {
		InstitutionClientEventDTO dto = new InstitutionClientEventDTO();
		dto.setClientType("EXCHANGE_SERVICE_USER");

		return dto;
	}

	@Nonnull
	@Override
	protected ClientEventHandler handler() {
		return handler;
	}
}
