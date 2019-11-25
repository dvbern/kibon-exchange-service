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
