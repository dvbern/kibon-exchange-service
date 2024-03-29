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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

import ch.dvbern.kibon.exchange.api.common.institution.KibonMandant;
import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.exchange.commons.verfuegungselbstbehaltgemeinde.GemeindeSelbstbehaltEventDTO;
import ch.dvbern.kibon.shared.filter.FilterController;
import ch.dvbern.kibon.shared.model.AbstractClientEntity_;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity_;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung_;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.model.Verfuegung_;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for {@link Verfuegung} handling.<br>
 * Uses the {@link ClientVerfuegung} table for speedy filtering/searching.
 */
@ApplicationScoped
public class VerfuegungService {

	private static final Logger LOG = LoggerFactory.getLogger(VerfuegungService.class);
	static final String SELBSTBEHALT_DURCH_GEMEINDE_PROPERTY = "keinSelbstbehaltDurchGemeinde";

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	VerfuegungConverter converter;

	/**
	 * Stores the verfuegung in response to the verfuegungCreated event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onVerfuegungCreated(@Nonnull VerfuegungEventDTO dto) {

		findVerfuegung(dto.getRefnr(), dto.getVersion())
			.ifPresentOrElse(
				existing -> this.updateVerfuegung(dto, existing),
				() -> this.createVerfuegung(dto)
			);
	}

	private void createVerfuegung(@Nonnull VerfuegungEventDTO dto) {
		Verfuegung verfuegung = converter.create(dto);

		em.persist(verfuegung);
	}

	private void updateVerfuegung(@Nonnull VerfuegungEventDTO dto, @Nonnull Verfuegung existingVerfuegung) {
		converter.update(existingVerfuegung, dto);

		em.merge(existingVerfuegung);
	}

	public void onGemeindeSelbstbehaltChanged(GemeindeSelbstbehaltEventDTO dto) {
		List<Verfuegung> verfuegungenToUpdate = findVerfuegungen(dto.getRefnr());
		verfuegungenToUpdate.forEach(verfuegung -> {
			ObjectNode kindNode = (ObjectNode) verfuegung.getKind();
			Objects.requireNonNull(kindNode);
			kindNode.put(SELBSTBEHALT_DURCH_GEMEINDE_PROPERTY, dto.getKeinSelbstbehaltDurchGemeinde());
			em.merge(verfuegung);
		});
	}

	@Nonnull
	private List<Verfuegung> findVerfuegungen(@Nonnull String refnr) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Verfuegung> query = cb.createQuery(Verfuegung.class);
		Root<Verfuegung> root = query.from(Verfuegung.class);

		ParameterExpression<String> refnrParam = cb.parameter(String.class, AbstractInstitutionPeriodeEntity_.REFNR);
		Predicate refnrPredicate = cb.equal(root.get(AbstractInstitutionPeriodeEntity_.refnr), refnrParam);

		query.where(refnrPredicate);

		return em.createQuery(query)
			.setParameter(refnrParam, refnr)
			.getResultList();
	}
	@Nonnull
	private Optional<Verfuegung> findVerfuegung(@Nonnull String refnr, int version) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Verfuegung> query = cb.createQuery(Verfuegung.class);
		Root<Verfuegung> root = query.from(Verfuegung.class);

		ParameterExpression<String> refnrParam = cb.parameter(String.class, AbstractInstitutionPeriodeEntity_.REFNR);
		Predicate refnrPredicate = cb.equal(root.get(AbstractInstitutionPeriodeEntity_.refnr), refnrParam);

		ParameterExpression<Integer> versionParam = cb.parameter(Integer.class, Verfuegung_.VERSION);
		Predicate versionPredicate = cb.equal(root.get(Verfuegung_.version), versionParam);

		query.where(refnrPredicate, versionPredicate);

		List<Verfuegung> verfuegungen = em.createQuery(query)
			.setParameter(refnrParam, refnr)
			.setParameter(versionParam, version)
			.getResultList();

		if (verfuegungen.isEmpty()) {
			return Optional.empty();
		}

		int count = verfuegungen.size();
		if (count > 1) {
			LOG.warn("Found {} Verfuegungen instead of 1 with refnr {} and version {}", count, refnr, version);
			verfuegungen.sort(Comparator.comparing(Verfuegung::getId).reversed());
		}

		return Optional.of(verfuegungen.get(0));
	}

	/**
	 * Delivers all {@link ClientVerfuegungDTO} for the given filter.
	 */
	@Transactional(TxType.MANDATORY)
	@Nonnull
	public List<ClientVerfuegungDTO> getAllForClient(
		@Nonnull FilterController<ClientVerfuegung, ClientVerfuegungDTO> filter) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClientVerfuegungDTO> query = cb.createQuery(ClientVerfuegungDTO.class);
		Root<ClientVerfuegung> root = query.from(ClientVerfuegung.class);
		Join<ClientVerfuegung, Verfuegung> verfuegung = root.join(ClientVerfuegung_.verfuegung);
		// is used by ClientGueltigkeitFilter (included in ClientVerfuegungFilter)
		root.join(AbstractClientEntity_.client);

		query.select(cb.construct(
			ClientVerfuegungDTO.class,
			root.get(AbstractClientEntity_.id),
			root.get(ClientVerfuegung_.since),
			verfuegung.get(AbstractInstitutionPeriodeEntity_.refnr),
			verfuegung.get(AbstractInstitutionPeriodeEntity_.institutionId),
			verfuegung.get(AbstractInstitutionPeriodeEntity_.periodeVon),
			verfuegung.get(AbstractInstitutionPeriodeEntity_.periodeBis),
			verfuegung.get(Verfuegung_.version),
			verfuegung.get(Verfuegung_.verfuegtAm),
			verfuegung.get(Verfuegung_.betreuungsArt),
			verfuegung.get(Verfuegung_.gemeindeBfsNr),
			verfuegung.get(Verfuegung_.gemeindeName),
			verfuegung.get(Verfuegung_.auszahlungAnEltern),
			verfuegung.get(Verfuegung_.kind),
			verfuegung.get(Verfuegung_.gesuchsteller),
			verfuegung.get(Verfuegung_.zeitabschnitte),
			verfuegung.get(Verfuegung_.ignorierteZeitabschnitte)
		));

		filter.setPredicate(query, root, cb);

		query.orderBy(cb.asc(root.get(ClientVerfuegung_.since)), cb.asc(root.get(AbstractClientEntity_.id)));

		TypedQuery<ClientVerfuegungDTO> q = em.createQuery(query);

		filter.setParameters(q);

		List<ClientVerfuegungDTO> resultList = q.getResultList();

		return resultList;
	}

	@Nonnull
	public List<Verfuegung> getAllForDashboard(
		@Nullable Long afterId,
		@Nullable Integer limit,
		@Nonnull KibonMandant mandant) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Verfuegung> query = cb.createQuery(Verfuegung.class);
		Root<Verfuegung> root = query.from(Verfuegung.class);
		Predicate mandantPredicate = cb.equal(root.get(Verfuegung_.mandant), mandant);

		if (afterId != null) {
			Predicate afterIdPredicate = cb.greaterThan(root.get(AbstractInstitutionPeriodeEntity_.id), afterId);
			query.where(mandantPredicate, afterIdPredicate);
		} else {
			query.where(mandantPredicate);
		}

		query.orderBy(cb.asc(root.get(AbstractInstitutionPeriodeEntity_.id)));

		TypedQuery<Verfuegung> q = em.createQuery(query);

		if (limit != null) {
			q.setMaxResults(limit);
		}

		return q.getResultList();
	}
}
