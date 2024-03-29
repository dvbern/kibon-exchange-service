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

package ch.dvbern.kibon.gemeindekennzahlen.service;

import java.time.LocalDate;
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

import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrageDTO;
import ch.dvbern.kibon.exchange.api.common.institution.KibonMandant;
import ch.dvbern.kibon.exchange.commons.gemeindekennzahlen.GemeindeKennzahlenEventDTO;
import ch.dvbern.kibon.gemeindekennzahlen.model.GemeindeKennzahlen;
import ch.dvbern.kibon.gemeindekennzahlen.model.GemeindeKennzahlen_;

@ApplicationScoped
public class GemeindeKennzahlenService {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	GemeindeKennzahlenConverter converter;

	/**
	 * Stores an gemeinde kennzahlen or updates an existing one, in response to an gemeindeKennzahlenChanged event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onGemeindeKennzahlenChanged(@Nonnull GemeindeKennzahlenEventDTO dto) {

		Optional<GemeindeKennzahlen> gemeindeKennzahlenOptional =
			getGemeindeKennzahlen(dto.getGemeindeUUID(), dto.getGesuchsperiodeStart());

		if (gemeindeKennzahlenOptional.isEmpty()) {
			GemeindeKennzahlen newGemeindeKennzahlen = converter.create(dto);
			em.persist(newGemeindeKennzahlen);
		} else {
			GemeindeKennzahlen gemeindeKennzahlen = gemeindeKennzahlenOptional.get();
			converter.update(gemeindeKennzahlen, dto);
			em.merge(gemeindeKennzahlen);
		}
	}

	/**
	 * Deletes an gemeinde kennzahlen, in response to an gemeindeKennzahlenRemoved event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onGemeindeKennzahlenRemoved(@Nonnull GemeindeKennzahlenEventDTO dto) {
		getGemeindeKennzahlen(dto.getGemeindeUUID(), dto.getGesuchsperiodeStart())
			.ifPresent(gemeindeKennzahlen -> em.remove(gemeindeKennzahlen));
	}

	/**
	 * Delivers all {@link ClientBetreuungAnfrageDTO} for the given filter.
	 */
	@Transactional(TxType.MANDATORY)
	public List<GemeindeKennzahlen> getAll(
		@Nullable Long afterId,
		@Nullable Integer limit,
		@Nonnull KibonMandant mandant) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<GemeindeKennzahlen> query = cb.createQuery(GemeindeKennzahlen.class);
		Root<GemeindeKennzahlen> root = query.from(GemeindeKennzahlen.class);

		query.orderBy(cb.asc(root.get(GemeindeKennzahlen_.sequenceId)));

		Predicate mandantPredicate = cb.equal(root.get(GemeindeKennzahlen_.mandant), mandant);

		if (afterId != null) {
			Predicate afterIdPredicate = cb.greaterThan(root.get(GemeindeKennzahlen_.sequenceId), afterId);
			query.where(mandantPredicate, afterIdPredicate);
		} else {
			query.where(mandantPredicate);
		}

		TypedQuery<GemeindeKennzahlen> q = em.createQuery(query);

		if (limit != null) {
			q.setMaxResults(limit);
		}

		List<GemeindeKennzahlen> resultList = q.getResultList();

		return resultList;
	}

	// TODO bfsNummer und gesuchsperiodeStart ist nicht zwingend eindeutisch in Mandanten-fähigem System. Auch nach
	//  Mandant oder einer globalen ID filtern.
	@Nonnull
	public Optional<GemeindeKennzahlen> getGemeindeKennzahlen(
		@Nonnull String gemeindeUUID,
		@Nonnull LocalDate gesuchsperiodeStart) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<GemeindeKennzahlen> query = cb.createQuery(GemeindeKennzahlen.class);
		Root<GemeindeKennzahlen> root = query.from(GemeindeKennzahlen.class);

		ParameterExpression<String> gemeindeUUIDParam = cb.parameter(String.class, GemeindeKennzahlen_.GEMEINDE_UU_ID);

		Predicate gemeindeUUIDPredicate = cb.equal(root.get(GemeindeKennzahlen_.gemeindeUUID), gemeindeUUIDParam);

		ParameterExpression<LocalDate> gesuchsperiodeStartParam =
			cb.parameter(LocalDate.class, GemeindeKennzahlen_.GESUCHSPERIODE_START);
		Predicate gesuchsperiodeStartPredicate =
			cb.equal(root.get(GemeindeKennzahlen_.gesuchsperiodeStart), gesuchsperiodeStartParam);

		query.where(gemeindeUUIDPredicate, gesuchsperiodeStartPredicate);

		return em.createQuery(query)
			.setParameter(gemeindeUUIDParam, gemeindeUUID)
			.setParameter(gesuchsperiodeStartParam, gesuchsperiodeStart)
			.setMaxResults(1)
			.getResultStream()
			.findFirst();
	}
}
