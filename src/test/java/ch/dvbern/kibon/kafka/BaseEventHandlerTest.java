package ch.dvbern.kibon.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.consumedmessage.ConsumedMessageService;
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
				@Nonnull EventType eventType,
				@Nonnull String dto) {
				if (throwOnProcessEvent) {
					throw new IllegalStateException("processEvent was called");
				}
			}
		};

		handler.consumedMessageService = strictMock(ConsumedMessageService.class);

		return handler;
	}

	@Test
	public void testIgnoreProcessedEvent() {
		BaseEventHandler<String> handler = createHandler(true);

		expect(handler.consumedMessageService.alreadyProcessed(EVENT_ID)).andReturn(true);
		// no calls on processEvent expected
		replay(handler.consumedMessageService);

		handler.onEvent("foo", EVENT_ID, LocalDateTime.now(), "foo", "bar");

		verify(handler.consumedMessageService);
	}

	@Test
	public void testProcessNewEvent() {
		BaseEventHandler<String> handler = createHandler(false);

		expect(handler.consumedMessageService.alreadyProcessed(EVENT_ID)).andReturn(false);
		handler.consumedMessageService.processed(EVENT_ID);
		expectLastCall();

		replay(handler.consumedMessageService);

		handler.onEvent("foo", EVENT_ID, LocalDateTime.now(), "foo", "bar");

		verify(handler.consumedMessageService);
	}
}
