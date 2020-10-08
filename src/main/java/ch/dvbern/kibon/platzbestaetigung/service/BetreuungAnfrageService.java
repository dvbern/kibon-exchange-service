/*
 * Copyright (C) 2020 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.platzbestaetigung.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungAnfrageEventDTO;
import ch.dvbern.kibon.platzbestaetigung.model.BetreuungAnfrage;
import ch.dvbern.kibon.platzbestaetigung.model.BetreuungAnfrage_;
import ch.dvbern.kibon.platzbestaetigung.model.ClientBetreuungAnfrage;
import ch.dvbern.kibon.platzbestaetigung.model.ClientBetreuungAnfrageDTO;
import ch.dvbern.kibon.platzbestaetigung.model.ClientBetreuungAnfrage_;
import ch.dvbern.kibon.platzbestaetigung.service.filter.ClientBetreuungAnfrageFilter;

@ApplicationScoped
public class BetreuungAnfrageService {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	BetreuungAnfrageConverter converter;

	/**
	 * Stores the BetreuungAnfrage in response to the betreuungAnfrageCreated event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onBetreuungAnfrageCreated(@Nonnull BetreuungAnfrageEventDTO dto) {
		BetreuungAnfrage betreuungAnfrage = converter.create(dto);

		em.persist(betreuungAnfrage);
	}

	/**
	 * Delivers all {@link ClientBetreuungAnfrageDTO} for the given filter.
	 */
	@Transactional(TxType.MANDATORY)
	public List<ClientBetreuungAnfrageDTO> getAllForClient(@Nonnull ClientBetreuungAnfrageFilter filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClientBetreuungAnfrageDTO> query = cb.createQuery(ClientBetreuungAnfrageDTO.class);
		Root<ClientBetreuungAnfrage> root = query.from(ClientBetreuungAnfrage.class);
		Join<ClientBetreuungAnfrage, BetreuungAnfrage> betreuungAnfrage =
			root.join(ClientBetreuungAnfrage_.betreuungAnfrage);

		query.select(cb.construct(
			ClientBetreuungAnfrageDTO.class,
			root.get(ClientBetreuungAnfrage_.id),
			betreuungAnfrage.get(BetreuungAnfrage_.refnr),
			betreuungAnfrage.get(BetreuungAnfrage_.institutionId),
			betreuungAnfrage.get(BetreuungAnfrage_.periodeVon),
			betreuungAnfrage.get(BetreuungAnfrage_.periodeBis),
			betreuungAnfrage.get(BetreuungAnfrage_.betreuungsArt),
			betreuungAnfrage.get(BetreuungAnfrage_.kind),
			betreuungAnfrage.get(BetreuungAnfrage_.gesuchsteller),
			betreuungAnfrage.get(BetreuungAnfrage_.abgelehntVonGesuchsteller)
		));

		filter.setPredicate(query, root, cb);

		query.orderBy(cb.asc(root.get(ClientBetreuungAnfrage_.id)));

		TypedQuery<ClientBetreuungAnfrageDTO> q = em.createQuery(query);

		filter.setParameters(q);

		List<ClientBetreuungAnfrageDTO> resultList = q.getResultList();

		return resultList;
	}
}
