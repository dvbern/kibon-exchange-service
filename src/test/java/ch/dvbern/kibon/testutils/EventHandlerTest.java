package ch.dvbern.kibon.testutils;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.consumedmessage.ConsumedMessageService;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

@SuppressWarnings("AbstractClassExtendsConcreteClass")
@ExtendWith(EasyMockExtension.class)
public abstract class EventHandlerTest<T> extends EasyMockSupport {

	protected static final UUID EVENT_ID = UUID.randomUUID();

	@SuppressWarnings({ "unused", "InstanceVariableMayNotBeInitialized" })
	@Mock(type = MockType.STRICT)
	private ConsumedMessageService messageLog;

	/**
	 * Basic test expectations for a new event that should be processed.
	 */
	protected void expectEventProcessing(@Nonnull String eventType, @Nonnull T dto) {

		expect(messageLog.alreadyProcessed(EVENT_ID)).andReturn(false);

		messageLog.processed(EVENT_ID);
		expectLastCall();

		replayAll();

		handler().onEvent("foo", EVENT_ID, LocalDateTime.now(), eventType, dto);

		verifyAll();
	}

	@Nonnull
	protected abstract BaseEventHandler<T> handler();
}
