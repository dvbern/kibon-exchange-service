package ch.dvbern.kibon.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.messagelog.MessageLog;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.strictMock;
import static org.easymock.EasyMock.verify;

class BaseEventHandlerTest {

	private static final UUID EVENT_ID = UUID.randomUUID();

	@Nonnull
	private BaseEventHandler<String> createHandler(boolean throwOnProcessEvent) {
		BaseEventHandler<String> handler = new BaseEventHandler<String>() {
			@Override
			protected void processEvent(
				@Nonnull UUID eventId,
				@Nonnull LocalDateTime eventTime,
				@Nonnull String eventType,
				@Nonnull String dto) {
				if (throwOnProcessEvent) {
					throw new IllegalStateException("processEvent was called");
				}
			}
		};

		handler.log = strictMock(MessageLog.class);

		return handler;
	}

	@Test
	public void testIgnoreProcessedEvent() {
		BaseEventHandler<String> handler = createHandler(true);

		expect(handler.log.alreadyProcessed(EVENT_ID)).andReturn(true);
		// no calls on processEvent expected
		replay(handler.log);

		handler.onEvent("foo", EVENT_ID, LocalDateTime.now(), "foo", "bar");

		verify(handler.log);
	}

	@Test
	public void testProcessNewEvent() {
		BaseEventHandler<String> handler = createHandler(false);

		expect(handler.log.alreadyProcessed(EVENT_ID)).andReturn(false);
		handler.log.processed(EVENT_ID);
		expectLastCall();

		replay(handler.log);

		handler.onEvent("foo", EVENT_ID, LocalDateTime.now(), "foo", "bar");

		verify(handler.log);
	}
}
