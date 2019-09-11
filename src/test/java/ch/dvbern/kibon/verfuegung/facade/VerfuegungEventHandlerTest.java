package ch.dvbern.kibon.verfuegung.facade;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.testutils.EventHandlerTest;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.expectLastCall;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
class VerfuegungEventHandlerTest extends EventHandlerTest<VerfuegungEventDTO> {

	@TestSubject
	private final VerfuegungEventHandler handler = new VerfuegungEventHandler();

	@SuppressWarnings({ "unused", "InstanceVariableMayNotBeInitialized" })
	@Mock(type = MockType.STRICT)
	private VerfuegungService verfuegungService;

	@Test
	public void testHandleVerfuegungVerfuegtEvent() {
		VerfuegungEventDTO dto = new VerfuegungEventDTO();

		verfuegungService.onVerfuegungCreated(dto);
		expectLastCall();

		expectEventProcessing("VerfuegungVerfuegt", dto);
	}

	@Test
	public void testProcessUnknownEvent() {
		expectEventProcessing("unknown", new VerfuegungEventDTO());
	}

	@Nonnull
	@Override
	protected VerfuegungEventHandler handler() {
		return handler;
	}
}
