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

package ch.dvbern.kibon.clients.facade;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import ch.dvbern.kibon.testutils.EventHandlerTest;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
class ClientEventHandlerTest extends EventHandlerTest<InstitutionClientEventDTO> {

	@TestSubject
	private final ClientEventHandler handler = new ClientEventHandler();

	@SuppressWarnings({ "unused", "InstanceVariableMayNotBeInitialized" })
	@Mock(type = MockType.STRICT)
	private ClientService clientService;

	@Test
	public void testHandleClientAddedEvent() {
		InstitutionClientEventDTO dto = expectedDTO();

		clientService.onClientAddedorModified(eq(dto), anyObject());
		expectLastCall();

		expectEventProcessing("ClientAdded", dto);
	}

	@Test
	public void testHandleClientRemovedEvent() {
		InstitutionClientEventDTO dto = expectedDTO();

		clientService.onClientRemoved(dto);
		expectLastCall();

		expectEventProcessing("ClientRemoved", dto);
	}

	@Test
	public void testProcessUnknownEvent() {
		expectEventProcessing("unknown", expectedDTO());
	}

	@Test
	public void testIgnoreUnknownClientType() {
		InstitutionClientEventDTO dto = new InstitutionClientEventDTO();
		dto.setClientType("NON_EXCHANGE_SERVICE_USER");

		expectEventProcessing("ClientAdded", dto);
	}

	@Nonnull
	private InstitutionClientEventDTO expectedDTO() {
		InstitutionClientEventDTO dto = new InstitutionClientEventDTO();
		dto.setClientType("EXCHANGE_SERVICE_USER");

		return dto;
	}

	@Nonnull
	@Override
	protected ClientEventHandler handler() {
		return handler;
	}
}
