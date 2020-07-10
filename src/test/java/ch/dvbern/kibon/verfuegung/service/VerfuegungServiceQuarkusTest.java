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

import java.util.List;

import javax.inject.Inject;

import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import ch.dvbern.kibon.testutils.TestcontainersEnvironment;
import ch.dvbern.kibon.testutils.TransactionHelper;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.service.filter.ClientVerfuegungFilter;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
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

	// Instead of using the @Transactional annotation or @TransactionalQuarkusTest we start the transactions manually.
	// Otherwise only one transaction for the whole test is started, such that the second call of on verfuegungService
	// returns excatly the same result as the first.
	@Test
	public void testGetAllForClient_doesNotReturnInactiveClients() throws Exception {
		ClientVerfuegungFilter kitAdmin = new ClientVerfuegungFilter("kitAdmin");

		// verify setup: when importing import-dev.sql, there should be 400 entries for client kitAdmin with
		// institution IDs 1 or 2
		assertThat(
			tx.newTransaction(() -> verfuegungService.getAllForClient(kitAdmin)),
			allOf(hasSize(400), everyItem(hasProperty("institutionId", anyOf(is("1"), is("2")))))
		);

		// deactive the client kitAdmin for institutionId 1
		InstitutionClientEventDTO dto = new InstitutionClientEventDTO("1", "kitAdmin", "EXCHANGE_SERVICE_USER");
		tx.newTransaction(() -> clientService.onClientRemoved(dto));

		// there should be no more results for insitutionId 1
		List<ClientVerfuegungDTO> allForClient = tx.newTransaction(() -> verfuegungService.getAllForClient(kitAdmin));
		assertThat(
			allForClient,
			allOf(hasSize(100), everyItem(hasProperty("institutionId", is("2"))))
		);
	}
}
