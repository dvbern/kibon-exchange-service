package ch.dvbern.kibon.verfuegung.facade;

import java.util.UUID;

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.messagelog.MessageLog;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.strictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.resetToStrict;
import static org.easymock.EasyMock.verify;

class VerfuegungEventHandlerTest {

	private static final UUID EVENT_ID = UUID.randomUUID();

	private final VerfuegungEventHandler handler = new VerfuegungEventHandler();

	private final VerfuegungService verfuegungService = strictMock(VerfuegungService.class);
	private final MessageLog messageLog = strictMock(MessageLog.class);

	@BeforeEach
	public void initMocks() {
		resetToStrict(verfuegungService, messageLog);

		handler.verfuegungService = verfuegungService;
		handler.log = messageLog;
	}

	@Test
	public void testIgnoreProcessedEvent() {
		expect(messageLog.alreadyProcessed(EVENT_ID)).andReturn(true);
		// no calls on verfuegungService expected
		replay(verfuegungService, messageLog);

		handler.onVerfuegungEvent("foo", EVENT_ID, "foo", new VerfuegungEventDTO());

		verify(verfuegungService, messageLog);
	}

	@Test
	public void testHandleVerfuegungVerfuegtEvent() {
		VerfuegungEventDTO dto = new VerfuegungEventDTO();

		expect(messageLog.alreadyProcessed(EVENT_ID)).andReturn(false);

		verfuegungService.verfuegungCreated(dto);
		expectLastCall();

		messageLog.processed(EVENT_ID);
		expectLastCall();

		replay(verfuegungService, messageLog);

		handler.onVerfuegungEvent("foo", EVENT_ID, "VerfuegungVerfuegt", dto);

		verify(verfuegungService, messageLog);
	}

	@Test
	public void testProcessUnknownEvent() {
		expect(messageLog.alreadyProcessed(EVENT_ID)).andReturn(false);

		messageLog.processed(EVENT_ID);
		expectLastCall();

		replay(verfuegungService, messageLog);

		handler.onVerfuegungEvent("foo", EVENT_ID, "unknown", new VerfuegungEventDTO());

		verify(verfuegungService, messageLog);
	}
}
