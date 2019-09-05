package ch.dvbern.kibon.institution.facade;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.service.InstitutionService;
import ch.dvbern.kibon.testutils.EventHandlerTest;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.expectLastCall;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
class InstitutionEventHandlerTest extends EventHandlerTest<InstitutionEventDTO> {

	@TestSubject
	private final InstitutionEventHandler handler = new InstitutionEventHandler();

	@SuppressWarnings({ "unused", "InstanceVariableMayNotBeInitialized" })
	@Mock(type = MockType.STRICT)
	private InstitutionService institutionService;

	@Test
	public void testHandleInstitutionChangedEvent() {
		InstitutionEventDTO dto = new InstitutionEventDTO();

		institutionService.institutionChanged(dto);
		expectLastCall();

		expectEventProcessing("InstitutionChanged", dto);
	}

	@Test
	public void testProcessUnknownEvent() {
		expectEventProcessing("unknown", new InstitutionEventDTO());
	}

	@Nonnull
	@Override
	protected InstitutionEventHandler handler() {
		return handler;
	}
}
