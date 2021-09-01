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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.exchange.commons.institutionclient.InstitutionClientEventDTO;
import ch.dvbern.kibon.util.DateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for {@link Client} handling.
 */
@ApplicationScoped
public class ClientService {

	private static final Logger LOG = LoggerFactory.getLogger(ClientService.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@Nonnull
	static ClientId toClientId(@Nonnull InstitutionClientEventDTO dto) {
		return new ClientId(dto.getClientName(), dto.getInstitutionId());
	}

	/**
	 * Stores the client in response to the clientAdded event.<br>
	 * If the client is already stored, it is not stored again but set to active in case it was inactive.
	 */
	@Transactional(TxType.MANDATORY)
	public void onClientAdded(@Nonnull InstitutionClientEventDTO dto, @Nonnull LocalDateTime eventTime) {
		Optional<Client> existingClient = find(toClientId(dto));

		if (existingClient.isPresent()) {
			reactivateClient(existingClient.get());
		} else {
			em.persist(new Client(toClientId(dto), eventTime, dto.getGueltigAb(), dto.getGueltigBis()));
		}
	}

	private void reactivateClient(@Nonnull Client existing) {
		if (existing.getActive()) {
			LOG.warn("Cannot reactivate already active client {}", existing);
		} else {
			// reactivate
			existing.setActive(true);
			em.merge(existing);
		}
	}

	/**
	 * Updates a client's gueltigkeit in response to the clientModified event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onClientModified(@Nonnull InstitutionClientEventDTO dto) {
		find(toClientId(dto)).ifPresentOrElse(
			client -> updateClientGueltigkeit(client, dto),
			() -> LOG.warn("Cannot update unknown client with name '{}' and institutionId '{}'",
				dto.getClientName(), dto.getInstitutionId()));
	}

	private void updateClientGueltigkeit(
		@Nonnull Client existingClient,
		@Nonnull InstitutionClientEventDTO dto) {

		DateRange existingGueltigkeit = DateRange.of(existingClient.getGueltigAb(), existingClient.getGueltigBis());
		DateRange newGueltigkeit = DateRange.of(dto.getGueltigAb(), dto.getGueltigBis());
		List<DateRange> previsoulyExcludedDates = newGueltigkeit.except(existingGueltigkeit);
		insertClientVerfuegungen(existingClient, previsoulyExcludedDates);

		existingClient.setGueltigAb(dto.getGueltigAb());
		existingClient.setGueltigBis(dto.getGueltigBis());
	}

	private void insertClientVerfuegungen(@Nonnull Client client, @Nonnull List<DateRange> searchRanges) {
		if (searchRanges.isEmpty()) {
			return;
		}

		String gueltigkeiten = searchRanges.stream()
			.map(d -> String.format(
				"(v.periodevon <= '%s' AND v.periodebis >= '%s')",
				d.getGueltigBis(),
				d.getGueltigAb()))
			.collect(Collectors.joining(" OR "));

		em.createNativeQuery(
				"INSERT INTO clientverfuegung (id, active, client_clientname, client_institutionid, verfuegung_id, "
					+ "since) (SELECT nextval('clientverfuegung_id_seq'), :active, :client, :institutionId, missing"
					+ ".id, now() FROM (SELECT DISTINCT v.id FROM verfuegung v WHERE v.institutionid = :institutionId "
					+ "AND ("
					+ gueltigkeiten
					+ ") ) AS missing );")
			.setParameter("active", client.getActive())
			.setParameter("client", client.getId().getClientName())
			.setParameter("institutionId", client.getId().getInstitutionId())
			.executeUpdate();
	}

	/**
	 * Sets a client to inactive in response to a clientRemoved event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onClientRemoved(@Nonnull InstitutionClientEventDTO dto) {
		Optional<Client> existingClient = find(toClientId(dto));

		if (existingClient.isPresent()) {
			inactivateClient(existingClient.get());
		} else {
			LOG.warn("Cannot inactivate unknown client with name '{}' and institutionId '{}'",
				dto.getClientName(), dto.getInstitutionId());
		}
	}

	private void inactivateClient(@Nonnull Client client) {
		if (client.getActive()) {
			client.setActive(false);
			em.merge(client);
		} else {
			LOG.warn("Cannot inactivate inactive client {}", client);
		}
	}

	@Nonnull
	public Optional<Client> find(@Nonnull ClientId id) {
		return Optional.ofNullable(em.find(Client.class, id));
	}

	@Nonnull
	public Optional<Client> findActive(@Nonnull ClientId id) {
		return find(id)
			.filter(Client::getActive);
	}

	@Nonnull
	public Client get(@Nonnull ClientId id) {
		return find(id).orElseThrow();
	}
}
