/*
 * Copyright (C) 2021 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.betreuung.facade;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.betreuung.service.BetreuungAnfrageService;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungAnfrageEventDTO;
import ch.dvbern.kibon.testutils.EventHandlerTest;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
public class BetreuungAnfrageEventHandlerTest extends EventHandlerTest<BetreuungAnfrageEventDTO> {

	@TestSubject
	private final BetreuungAnfrageEventHandler handler = new BetreuungAnfrageEventHandler();

	@SuppressWarnings({ "unused", "InstanceVariableMayNotBeInitialized" })
	@Mock(type = MockType.STRICT)
	private BetreuungAnfrageService betreuungAnfrageService;

	@Test
	public void testHandleVerfuegungVerfuegtEvent() {
		BetreuungAnfrageEventDTO dto = new BetreuungAnfrageEventDTO();

		//noinspection ConstantConditions
		betreuungAnfrageService.onBetreuungAnfrageCreated(eq(dto), anyObject(LocalDateTime.class));
		expectLastCall();

		expectEventProcessing("BetreuungAnfrageAdded", dto);
	}

	@Test
	public void testProcessUnknownEvent() {
		expectEventProcessing("unknown", new BetreuungAnfrageEventDTO());
	}

	@Nonnull
	@Override
	protected BetreuungAnfrageEventHandler handler() {
		return handler;
	}

}
