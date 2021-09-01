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

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.shared.model.Gesuchsperiode;
import ch.dvbern.kibon.shared.model.Gesuchsperiode_;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung_;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldungDTO;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung_;
import ch.dvbern.kibon.tagesschulen.service.filter.ClientAnmeldungFilter;

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
		//sucht ob es schon eine Anmeldung mit selbe Refnummer gibt
		Anmeldung lastExistingAnmeldung = getLatestAnmeldung(dto.getAnmeldungsDetails().getRefnr());
		Anmeldung newAnmeldung = converter.create(dto, eventTime);

		if (!(lastExistingAnmeldung != null && lastExistingAnmeldung.compareTo(newAnmeldung) == 0)) {
			em.persist(newAnmeldung);
		}
	}

	private Anmeldung getLatestAnmeldung(String refnr) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Anmeldung> query = cb.createQuery(Anmeldung.class);
		Root<Anmeldung> root = query.from(Anmeldung.class);

		Predicate refnrPredicate = cb.equal(root.get(Anmeldung_.refnr), refnr);
		query.where(refnrPredicate);
		query.orderBy(cb.desc(root.get(Anmeldung_.eventTimestamp)));

		return em.createQuery(query).getResultList().stream().findFirst().orElse(null);
	}

	/**
	 * Delivers all {@link ClientAnmeldungDTO} for the given filter.
	 */
	@Transactional(TxType.MANDATORY)
	public List<ClientAnmeldungDTO> getAllForClient(@Nonnull ClientAnmeldungFilter filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClientAnmeldungDTO> query = cb.createQuery(ClientAnmeldungDTO.class);
		Root<ClientAnmeldung> root = query.from(ClientAnmeldung.class);
		Join<ClientAnmeldung, Anmeldung> anmeldungJoin =
			root.join(ClientAnmeldung_.anmeldung);

		Join<Anmeldung, Gesuchsperiode> anmeldungGesuchsperiodeJoin =
			anmeldungJoin.join(Anmeldung_.gesuchsperiode);

		query.select(cb.construct(
			ClientAnmeldungDTO.class,
			root.get(ClientAnmeldung_.id),
			anmeldungJoin.get(Anmeldung_.institutionId),
			anmeldungJoin.get(Anmeldung_.refnr),
			anmeldungJoin.get(Anmeldung_.version),
			anmeldungJoin.get(Anmeldung_.eventTimestamp),
			anmeldungGesuchsperiodeJoin.get(Gesuchsperiode_.gueltigAb),
			anmeldungGesuchsperiodeJoin.get(Gesuchsperiode_.gueltigBis),
			anmeldungJoin.get(Anmeldung_.kind),
			anmeldungJoin.get(Anmeldung_.gesuchsteller),
			anmeldungJoin.get(Anmeldung_.planKlasse),
			anmeldungJoin.get(Anmeldung_.abholung),
			anmeldungJoin.get(Anmeldung_.abweichungZweitesSemester),
			anmeldungJoin.get(Anmeldung_.bemerkung),
			anmeldungJoin.get(Anmeldung_.anmeldungZurueckgezogen),
			anmeldungJoin.get(Anmeldung_.eintrittsdatum),
			anmeldungJoin.get(Anmeldung_.anmeldungModule)
		));

		filter.setPredicate(query, root, cb);

		query.orderBy(cb.asc(root.get(ClientAnmeldung_.id)));

		TypedQuery<ClientAnmeldungDTO> q = em.createQuery(query);

		filter.setParameters(q);

		List<ClientAnmeldungDTO> resultList = q.getResultList();

		return resultList;
	}

	/*@Transactional(TxType.MANDATORY)
	public List<ClientAnmeldungDTO> getAllForClient(@Nonnull ClientAnmeldungFilter filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClientAnmeldung> query = cb.createQuery(ClientAnmeldung.class);
		Root<ClientAnmeldung> root = query.from(ClientAnmeldung.class);
		filter.setPredicate(query, root, cb);

		query.orderBy(cb.asc(root.get(ClientAnmeldung_.id)));

		TypedQuery<ClientAnmeldung> q = em.createQuery(query);

		filter.setParameters(q);

		List<ClientAnmeldung> resultList = q.getResultList();

		return resultList.stream().map(converter::toClientAnmeldungDTO).collect(Collectors.toList());
	}*/
}
