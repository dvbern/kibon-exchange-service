package ch.dvbern.kibon.clients.service;

import java.time.LocalDateTime;
import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import ch.dvbern.kibon.testutils.EasyMockExtension;
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
	public void testClientAdded_shouldIgnoreActiveClient() {
		InstitutionClientEventDTO dto = createDTO();

		Client existingClient = toClient(dto);
		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(existingClient);

		replay(em);

		service.clientAdded(dto, LocalDateTime.now());

		verify(em);
	}

	@Test
	public void testClientAdded_shouldAddNewClient() {
		InstitutionClientEventDTO dto = createDTO();

		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(null);

		Client expectedClient = toClient(dto);
		em.persist(cmp(expectedClient, CLIENT_COMPARATOR, LogicalOperator.EQUAL));
		expectLastCall();

		replay(em);

		service.clientAdded(dto, expectedClient.getGrantedSince());

		verify(em);
	}

	@Test
	public void testClientAdded_shouldSetGrantedSinceFromEventTime() {
		InstitutionClientEventDTO dto = createDTO();

		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(null);

		LocalDateTime eventTime = LocalDateTime.now();
		Client expectedClient = toClient(dto);
		expectedClient.setGrantedSince(eventTime);

		em.persist(cmp(expectedClient, CLIENT_COMPARATOR, LogicalOperator.EQUAL));
		expectLastCall();

		replay(em);

		service.clientAdded(dto, eventTime);

		verify(em);
	}

	@Test
	public void testClientAdded_shouldReactivateInactiveClient() {
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

		service.clientAdded(dto, LocalDateTime.now());

		verify(em);
	}

	@Test
	public void testClientRemoved_shouldIgnoreUnknownClient() {
		InstitutionClientEventDTO dto = createDTO();

		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(null);

		replay(em);

		service.clientRemoved(dto);

		verify(em);
	}

	@Test
	public void testClientRemoved_shouldDeactivateActiveClient() {
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

		service.clientRemoved(dto);

		verify(em);
	}

	@Test
	public void testClientRemoved_shouldIgnoreInactiveClient() {
		InstitutionClientEventDTO dto = createDTO();

		Client inactiveClient = toClient(dto);
		inactiveClient.setActive(false);
		expect(em.find(Client.class, toClientId(dto)))
			.andReturn(inactiveClient);

		replay(em);

		service.clientRemoved(dto);

		verify(em);
	}

	@Nonnull
	private InstitutionClientEventDTO createDTO() {
		return new InstitutionClientEventDTO("1", "foo", "bar");
	}

	@Nonnull
	private Client toClient(@Nonnull InstitutionClientEventDTO dto) {
		Client client = new Client();
		client.setId(toClientId(dto));

		return client;
	}
}
