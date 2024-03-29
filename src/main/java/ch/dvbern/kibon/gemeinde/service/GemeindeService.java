/*
 * Copyright (C) 2022 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.gemeinde.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.exchange.api.common.dashboard.gemeinde.GemeindeDTO;
import ch.dvbern.kibon.exchange.api.common.institution.KibonMandant;
import ch.dvbern.kibon.exchange.commons.gemeinde.GemeindeEventDTO;
import ch.dvbern.kibon.gemeinde.model.Gemeinde;
import ch.dvbern.kibon.gemeinde.model.Gemeinde_;

@ApplicationScoped
public class GemeindeService {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	GemeindeConverter converter;

	/**
	 * Stores an gemeinde or updates an existing one, in response to an gemeindeChanged event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onGemeindeChanged(@Nonnull GemeindeEventDTO dto) {

		Optional<Gemeinde> gemeindeOptional = getGemeindeByGemeindeUUID(dto.getGemeindeUUID());

		if (gemeindeOptional.isEmpty()) {
			Gemeinde newGemeinde = converter.create(dto);
			em.persist(newGemeinde);
		} else {
			Gemeinde gemeinde = gemeindeOptional.get();
			converter.update(gemeinde, dto);
			em.merge(gemeinde);
		}
	}

	@Transactional(TxType.MANDATORY)
	public List<GemeindeDTO> getAll(@Nullable Long afterId, @Nullable Integer limit, @Nonnull KibonMandant mandant) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<GemeindeDTO> query = cb.createQuery(GemeindeDTO.class);
		Root<Gemeinde> root = query.from(Gemeinde.class);

		query.select(cb.construct(
			GemeindeDTO.class,
			root.get(Gemeinde_.sequenceId),
			root.get(Gemeinde_.name),
			root.get(Gemeinde_.betreuungsgutscheineAnbietenAb),
			root.get(Gemeinde_.gueltigBis),
			root.get(Gemeinde_.bfsNummer)
		));

		query.orderBy(cb.asc(root.get(Gemeinde_.sequenceId)));
		Predicate mandantPredicate = cb.equal(root.get(Gemeinde_.mandant), mandant);
		Predicate nurBGAngebot = cb.isTrue(root.get(Gemeinde_.angebotBG));

		if (afterId != null) {
			Predicate afterIdPredicate = cb.greaterThan(root.get(Gemeinde_.sequenceId), afterId);
			query.where(nurBGAngebot, mandantPredicate, afterIdPredicate);
		} else {
			query.where(nurBGAngebot, mandantPredicate);
		}

		TypedQuery<GemeindeDTO> q = em.createQuery(query);

		if (limit != null) {
			q.setMaxResults(limit);
		}

		List<GemeindeDTO> resultList = q.getResultList();

		return resultList;
	}

	@Nonnull
	public Optional<Gemeinde> getGemeindeByGemeindeUUID(@Nonnull String gemeindeUUID) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Gemeinde> query = cb.createQuery(Gemeinde.class);
		Root<Gemeinde> root = query.from(Gemeinde.class);

		ParameterExpression<String> gemeindeUUIDParam = cb.parameter(String.class, Gemeinde_.GEMEINDE_UU_ID);

		Predicate gemeindeUUIDPredicate = cb.equal(root.get(Gemeinde_.gemeindeUUID), gemeindeUUIDParam);

		query.where(gemeindeUUIDPredicate);

		return em.createQuery(query)
			.setParameter(gemeindeUUIDParam, gemeindeUUID)
			.setMaxResults(1)
			.getResultStream()
			.findFirst();
	}
}
