/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.dvbern.kibon.testutils;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.consumedmessage.ConsumedMessageService;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import org.easymock.EasyMockExtension;
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
