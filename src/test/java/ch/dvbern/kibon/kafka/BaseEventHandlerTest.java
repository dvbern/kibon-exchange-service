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
		BaseEventHandler<String> handler = new BaseEventHandler<>() {
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
