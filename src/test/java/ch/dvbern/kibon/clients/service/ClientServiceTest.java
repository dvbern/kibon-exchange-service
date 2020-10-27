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

package ch.dvbern.kibon.clients.service;

import java.time.LocalDateTime;
import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import org.easymock.EasyMockExtension;
import org.easymock.LogicalOperator;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static ch.dvbern.kibon.clients.service.ClientService.toClientId;
import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

@ExtendWith(EasyMockExtension.class)
class ClientServiceTest {

	private static final Comparator<ClientId> CLIENT_ID_COMPARATOR = Comparator
		.comparing(ClientId::getClientName)
		.thenComparing(ClientId::getInstitutionId);

	private static final Comparator<Client> CLIENT_COMPARATOR = Comparator
		.comparing(Client::getId, CLIENT_ID_COMPARATOR)
		.thenComparing(Client::getGrantedSince)
		.thenComparing(Client::getActive);

	@TestSubject
	private final ClientService service = new ClientService();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private EntityManager em;

	@Test
	public void testOnClientAdded_shouldIgnoreActiveClient() {
		InstitutionClientEventDTO dto = createDTO();

		Client existingClient = toClient(dto);
		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(existingClient);

		replay(em);

		service.onClientAdded(dto, LocalDateTime.now());

		verify(em);
	}

	@Test
	public void testOnClientAdded_shouldAddNewClient() {
		InstitutionClientEventDTO dto = createDTO();

		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(null);

		Client expectedClient = toClient(dto);
		em.persist(cmp(expectedClient, CLIENT_COMPARATOR, LogicalOperator.EQUAL));
		expectLastCall();

		replay(em);

		service.onClientAdded(dto, expectedClient.getGrantedSince());

		verify(em);
	}

	@Test
	public void testOnClientAdded_shouldSetGrantedSinceFromEventTime() {
		InstitutionClientEventDTO dto = createDTO();

		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(null);

		LocalDateTime eventTime = LocalDateTime.now();
		Client expectedClient = toClient(dto);
		expectedClient.setGrantedSince(eventTime);

		em.persist(cmp(expectedClient, CLIENT_COMPARATOR, LogicalOperator.EQUAL));
		expectLastCall();

		replay(em);

		service.onClientAdded(dto, eventTime);

		verify(em);
	}

	@Test
	public void testOnClientAdded_shouldReactivateInactiveClient() {
		InstitutionClientEventDTO dto = createDTO();
		LocalDateTime grantedSince = LocalDateTime.now().minusHours(5);

		Client existingClient = toClient(dto);
		existingClient.setActive(false);
		existingClient.setGrantedSince(grantedSince);
		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(existingClient);

		Client expectedClient = toClient(dto);
		expectedClient.setActive(true);
		expectedClient.setGrantedSince(grantedSince);
		expect(em.merge(cmp(expectedClient, CLIENT_COMPARATOR, LogicalOperator.EQUAL)))
			.andReturn(existingClient);

		replay(em);

		service.onClientAdded(dto, LocalDateTime.now());

		verify(em);
	}

	@Test
	public void testOnClientRemoved_shouldIgnoreUnknownClient() {
		InstitutionClientEventDTO dto = createDTO();

		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(null);

		replay(em);

		service.onClientRemoved(dto);

		verify(em);
	}

	@Test
	public void testOnClientRemoved_shouldDeactivateActiveClient() {
		InstitutionClientEventDTO dto = createDTO();

		Client inactiveClient = toClient(dto);
		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(inactiveClient);

		Client expectedClient = toClient(dto);
		expectedClient.setActive(false);
		expectedClient.setGrantedSince(inactiveClient.getGrantedSince());
		expect(em.merge(cmp(expectedClient, CLIENT_COMPARATOR, LogicalOperator.EQUAL)))
			.andReturn(expectedClient);

		replay(em);

		service.onClientRemoved(dto);

		verify(em);
	}

	@Test
	public void testOnClientRemoved_shouldIgnoreInactiveClient() {
		InstitutionClientEventDTO dto = createDTO();

		Client inactiveClient = toClient(dto);
		inactiveClient.setActive(false);
		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(inactiveClient);

		replay(em);

		service.onClientRemoved(dto);

		verify(em);
	}

	@Nonnull
	private InstitutionClientEventDTO createDTO() {
		return new InstitutionClientEventDTO("1", "foo", "bar", null, null);
	}

	@Nonnull
	private Client toClient(@Nonnull InstitutionClientEventDTO dto) {
		Client client = new Client();
		client.setId(toClientId(dto));

		return client;
	}
}
