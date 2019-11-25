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

		institutionService.onInstitutionChanged(dto);
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
