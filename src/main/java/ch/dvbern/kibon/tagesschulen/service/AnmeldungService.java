/*
 * Copyright (C) 2021 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.tagesschulen.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.api.shared.ClientInstitutionFilterParams;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.shared.filter.FilterController;
import ch.dvbern.kibon.shared.filter.FilterControllerFactory;
import ch.dvbern.kibon.shared.model.AbstractClientEntity_;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity_;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung_;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldungDTO;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung_;

@ApplicationScoped
public class AnmeldungService {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	AnmeldungConverter converter;

	@Transactional(TxType.MANDATORY)
	public void onAnmeldungTagesschule(@Nonnull TagesschuleAnmeldungEventDTO dto, @Nonnull LocalDateTime eventTime) {
		Optional<Anmeldung> lastExistingAnmeldung = getLatestAnmeldung(dto.getAnmeldungsDetails().getRefnr());
		Anmeldung newAnmeldung = converter.create(dto, eventTime);

		if (lastExistingAnmeldung.isEmpty()) {
			em.persist(newAnmeldung);

			return;
		}

		Anmeldung last = lastExistingAnmeldung.get();
		if (Anmeldung.COMPARATOR.compare(last, newAnmeldung) == 0) {
			// don't trigger a new entry in ClientAnmeldung table: just update status & tarife of lastExistingAnmeldung
			last.setStatus(newAnmeldung.getStatus());
			last.setEventTimestamp(eventTime);
			last.setTarife(newAnmeldung.getTarife());
			em.merge(last);
		} else {
			// some essential data in anmelung changed -> re-export
			em.persist(newAnmeldung);
		}
	}

	@Nonnull
	public Optional<Anmeldung> getLatestAnmeldung(@Nonnull String refnr) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Anmeldung> query = cb.createQuery(Anmeldung.class);
		Root<Anmeldung> root = query.from(Anmeldung.class);

		ParameterExpression<String> refnrParam = cb.parameter(String.class, AbstractInstitutionPeriodeEntity_.REFNR);

		Predicate refnrPredicate = cb.equal(root.get(AbstractInstitutionPeriodeEntity_.refnr), refnrParam);

		query.where(refnrPredicate)
			.orderBy(cb.desc(root.get(AbstractInstitutionPeriodeEntity_.id)));

		return em.createQuery(query)
			.setParameter(refnrParam, refnr)
			.setMaxResults(1)
			.getResultStream()
			.findFirst();
	}

	/**
	 * Delivers latest {@link ClientAnmeldungDTO} for the given refnr.
	 */
	@Transactional(TxType.MANDATORY)
	public Optional<ClientAnmeldungDTO> getLatestClientAnmeldung(@Nonnull String clientName, @Nonnull String refnr) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<ClientAnmeldungDTO> query = cb.createQuery(ClientAnmeldungDTO.class);
		Root<ClientAnmeldung> root = query.from(ClientAnmeldung.class);

		select(query, root, cb);

		ClientInstitutionFilterParams filterParams = new ClientInstitutionFilterParams();
		filterParams.setLimit(1);
		filterParams.setRefnr(refnr);

		FilterController<ClientAnmeldung, ClientAnmeldungDTO> filter =
			FilterControllerFactory.anmeldungenFilter(clientName, filterParams);

		filter.setPredicate(query, root, cb);

		query.orderBy(cb.desc(root.get(AbstractClientEntity_.id)));

		TypedQuery<ClientAnmeldungDTO> q = em.createQuery(query);

		filter.setParameters(q);

		return q.getResultStream().findFirst();
	}

	/**
	 * Delivers all {@link ClientAnmeldungDTO} for the given filter.
	 */
	@Transactional(TxType.MANDATORY)
	public List<ClientAnmeldungDTO> getAllForClient(
		@Nonnull FilterController<ClientAnmeldung, ClientAnmeldungDTO> filter) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<ClientAnmeldungDTO> query = cb.createQuery(ClientAnmeldungDTO.class);
		Root<ClientAnmeldung> root = query.from(ClientAnmeldung.class);

		select(query, root, cb);

		filter.setPredicate(query, root, cb);

		query.orderBy(cb.asc(root.get(AbstractClientEntity_.id)));

		TypedQuery<ClientAnmeldungDTO> q = em.createQuery(query);

		filter.setParameters(q);

		List<ClientAnmeldungDTO> resultList = q.getResultList();

		return resultList;
	}

	private void select(
		@Nonnull CriteriaQuery<ClientAnmeldungDTO> query,
		@Nonnull Root<ClientAnmeldung> root,
		@Nonnull CriteriaBuilder cb) {

		Join<ClientAnmeldung, Anmeldung> anmeldungJoin = root.join(ClientAnmeldung_.anmeldung);

		query.select(cb.construct(
			ClientAnmeldungDTO.class,
			root.get(AbstractClientEntity_.id),
			anmeldungJoin.get(AbstractInstitutionPeriodeEntity_.institutionId),
			anmeldungJoin.get(AbstractInstitutionPeriodeEntity_.refnr),
			anmeldungJoin.get(Anmeldung_.version),
			anmeldungJoin.get(Anmeldung_.status),
			anmeldungJoin.get(Anmeldung_.eventTimestamp),
			anmeldungJoin.get(AbstractInstitutionPeriodeEntity_.periodeVon),
			anmeldungJoin.get(AbstractInstitutionPeriodeEntity_.periodeBis),
			anmeldungJoin.get(Anmeldung_.kind),
			anmeldungJoin.get(Anmeldung_.gesuchsteller),
			anmeldungJoin.get(Anmeldung_.gesuchsteller2),
			anmeldungJoin.get(Anmeldung_.planKlasse),
			anmeldungJoin.get(Anmeldung_.abholung),
			anmeldungJoin.get(Anmeldung_.abweichungZweitesSemester),
			anmeldungJoin.get(Anmeldung_.bemerkung),
			anmeldungJoin.get(Anmeldung_.anmeldungZurueckgezogen),
			anmeldungJoin.get(Anmeldung_.eintrittsdatum),
			anmeldungJoin.get(Anmeldung_.module)
		));
	}
}
