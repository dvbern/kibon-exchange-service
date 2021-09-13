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

package ch.dvbern.kibon.verfuegung.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.testutils.TestcontainersEnvironment;
import ch.dvbern.kibon.testutils.TransactionHelper;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.service.filter.ClientVerfuegungFilter;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventTestUtil.createDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTestResource(TestcontainersEnvironment.class)
@QuarkusTest
class VerfuegungServiceQuarkusTest {

	@Inject
	VerfuegungService verfuegungService;

	@Inject
	ClientService clientService;

	@Inject
	TransactionHelper tx;

	@Inject
	EntityManager em;

	// Instead of using the @Transactional annotation or @TestTransaction we start the transactions manually.
	// Otherwise only one transaction for the whole test is started, such that the second call of on verfuegungService
	// returns excatly the same result as the first.
	@Test
	public void testGetAllForClient_doesNotReturnInactiveClients() throws Exception {
		ClientVerfuegungFilter kitAdmin = new ClientVerfuegungFilter("kitAdmin");

		// verify setup: when importing import-test.sql, there should be 400 entries for client kitAdmin with
		// institution IDs 1 or 2
		assertThat(
			tx.newTransaction(() -> verfuegungService.getAllForClient(kitAdmin)),
			allOf(hasSize(400), everyItem(hasProperty("institutionId", anyOf(is("1"), is("2")))))
		);

		// deactivate the client kitAdmin for institutionId 1
		InstitutionClientEventDTO dto =
			new InstitutionClientEventDTO("1", "kitAdmin", "EXCHANGE_SERVICE_USER", null, null);
		tx.newTransaction(() -> clientService.onClientRemoved(dto));

		// there should be no more results for insitutionId 1
		List<ClientVerfuegungDTO> allForClient = tx.newTransaction(() -> verfuegungService.getAllForClient(kitAdmin));
		assertThat(
			allForClient,
			allOf(hasSize(100), everyItem(hasProperty("institutionId", is("2"))))
		);

		// restore client (to make sure following integration tests get the expected DB setup)
		tx.newTransaction(() -> clientService.onClientAdded(dto, LocalDateTime.now()));
	}

	/**
	 * Test setup (import-test.sql) contains 300 Verfuegungen for institution 1 and 100 Verfuegungen for
	 * institution 2.
	 * Client gueltigkeit of institution 1 is controlled through the input parameters.
	 * Client gueltigkeit of institution 2 is set to [2021-01-01, NULL).
	 */
	@ParameterizedTest
	@CsvSource(value = {
		"NULL, NULL, 400",
		"NULL, 2019-07-31, 100",
		"2020-08-01, NULL, 100",
		"2000-01-01, NULL, 400",
		"2020-01-01, 2020-01-31, 400",
		"2020-08-01, 2022-07-31, 100"
	},
		nullValues = "NULL")
	void testGetAllForClient_filtersByClientGueltigkeit(
		@Nullable LocalDate von,
		@Nullable LocalDate bis,
		int expectedResults) throws Exception {

		ClientVerfuegungFilter kitAdmin = new ClientVerfuegungFilter("kitAdmin");

		// change gueltigkeit of client
		InstitutionClientEventDTO dto =
			new InstitutionClientEventDTO("1", "kitAdmin", "EXCHANGE_SERVICE_USER", von, bis);
		tx.newTransaction(() -> clientService.onClientModified(dto));

		// there should be no more results for insitutionId 1
		List<ClientVerfuegungDTO> allForClient = tx.newTransaction(() -> verfuegungService.getAllForClient
			(kitAdmin));
		assertThat(allForClient, hasSize(expectedResults));

		// restore gueltigkeit of client (@TestTransaction and @ParametrizedTest does not seem to work)
		tx.newTransaction(() -> {
			Client client = clientService.get(new ClientId("kitAdmin", "1"));
			client.setGueltigAb(null);
			client.setGueltigBis(null);
			em.merge(client);
		});
	}

	// Rationale: a client gets all verfuegugen, filtering with after_id.
	// an old verfuegung has an ID, even though it's not accessible due to client gueltigkeit.
	// when the gueltigkeit changes, verfuegungen should be provided with a new id.
	@Test
	void testGetAllForClient_exportsPreviouslyUnaccessibleVerfuegugenWithNewId() throws Exception {
		String clientName = "newClient";
		String institutionId = "2";
		ClientVerfuegungFilter newClient = new ClientVerfuegungFilter(clientName);

		addClient(clientName, institutionId);
		addVerfuegung(institutionId);

		// verify setup: only the just added Verfuegung should be withing gueltigkeit
		List<ClientVerfuegungDTO> allForClient = tx.newTransaction(() -> verfuegungService.getAllForClient(newClient));
		assertThat(allForClient, hasSize(1));
		long maxId = allForClient.stream()
			.mapToLong(ClientVerfuegungDTO::getId)
			.max()
			.orElseThrow();

		ClientVerfuegungFilter newClientAfterId = new ClientVerfuegungFilter(clientName, maxId, null);
		assertThat(tx.newTransaction(() -> verfuegungService.getAllForClient(newClientAfterId)), is(empty()));

		// enable client for old verfuegungen
		enableClientForOldVerfuegungen(clientName, institutionId);

		assertThat(tx.newTransaction(() -> verfuegungService.getAllForClient(newClientAfterId)), hasSize(100));

		// restore database
		tx.newTransaction(() -> {
			Verfuegung verfuegung = em.createQuery(
				"SELECT cv.verfuegung FROM ClientVerfuegung cv WHERE cv.id = :clientVerfuegungId",
				Verfuegung.class)
				.setParameter("clientVerfuegungId", maxId)
				.getSingleResult();

			ClientId clientId = new ClientId(clientName, institutionId);

			em.createQuery("DELETE FROM ClientVerfuegung WHERE client.id = :clientId OR verfuegung = :verfuegung")
				.setParameter("clientId", clientId)
				.setParameter("verfuegung", verfuegung)
				.executeUpdate();

			em.createQuery("DELETE FROM ClientBetreuungAnfrage WHERE client.id = :clientId")
				.setParameter("clientId", clientId)
				.executeUpdate();

			em.remove(verfuegung);

			em.createQuery("DELETE FROM Client WHERE id = :clientId")
				.setParameter("clientId", clientId)
				.executeUpdate();
		});
	}

	private void addClient(@Nonnull String clientName, @Nonnull String institutionId) {
		LocalDate gueltigAb = LocalDate.of(2022, 8, 1);
		InstitutionClientEventDTO dto =
			new InstitutionClientEventDTO(institutionId, clientName, "EXCHANGE_SERVICE_USER", gueltigAb, null);

		tx.newTransaction(() -> clientService.onClientAdded(dto, LocalDateTime.now()));
	}

	private void addVerfuegung(@Nonnull String institutionId) {
		VerfuegungEventDTO verfuegungEventDTO = createDTO();
		verfuegungEventDTO.setVon(LocalDate.of(2022, 8, 1));
		verfuegungEventDTO.setBis(LocalDate.of(2023, 7, 31));
		verfuegungEventDTO.setInstitutionId(institutionId);

		tx.newTransaction(() -> verfuegungService.onVerfuegungCreated(verfuegungEventDTO));
	}

	private void enableClientForOldVerfuegungen(@Nonnull String clientName, @Nonnull String institutionId) {
		InstitutionClientEventDTO dto =
			new InstitutionClientEventDTO(institutionId, clientName, "EXCHANGE_SERVICE_USER", null, null);

		tx.newTransaction(() -> clientService.onClientModified(dto));
	}
}
