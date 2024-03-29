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

package ch.dvbern.kibon.institution.service;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.exchange.api.common.institution.ClientInstitutionDTO;
import ch.dvbern.kibon.exchange.api.common.institution.InstitutionDTO;
import ch.dvbern.kibon.exchange.api.common.institution.KibonMandant;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionStatus;
import ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.model.Institution_;
import ch.dvbern.kibon.institution.model.KontaktAngaben;
import ch.dvbern.kibon.institution.model.KontaktAngaben_;
import ch.dvbern.kibon.util.ConstantsUtil;

/**
 * Service responsible for {@link Institution} handling.
 */
@ApplicationScoped
public class InstitutionService {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	InstitutionConverter converter;

	/**
	 * Stores an institution or updates an existing one, in response to an institutionChanged event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onInstitutionChanged(@Nonnull InstitutionEventDTO dto) {

		Institution institution = em.find(Institution.class, dto.getId());
		if (institution == null) {
			Institution newInstitution = converter.create(dto);

			em.persist(newInstitution);
		} else {
			converter.update(institution, dto);

			em.merge(institution);
		}
	}

	@Nonnull
	public List<Institution> getForFamilyPortal() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Institution> query = cb.createQuery(Institution.class);
		Root<Institution> root = query.from(Institution.class);

		//noinspection rawtypes
		ParameterExpression<Set> betreuungsArtParam = cb.parameter(Set.class, "betreuungsArtParam");
		Predicate betreuungArtPredicate = root.get(Institution_.betreuungsArt).in(betreuungsArtParam);

		ParameterExpression<InstitutionStatus> statusParam = cb.parameter(InstitutionStatus.class, "statusParam");
		Predicate statusPredicate = cb.equal(root.get(Institution_.status), statusParam);
		Predicate mandantPredicate = cb.equal(root.get(Institution_.mandant), KibonMandant.BERN);
		Predicate unbekannteInsti = root.get(Institution_.id)
			.in(ConstantsUtil.ALL_UNKNOWN_BE_INSTITUTION_IDS)
			.not();
		query.where(betreuungArtPredicate, statusPredicate, mandantPredicate, unbekannteInsti);

		Set<BetreuungsangebotTyp> familyPortalSet =
			EnumSet.of(BetreuungsangebotTyp.KITA, BetreuungsangebotTyp.TAGESFAMILIEN);

		return em.createQuery(query)
			.setParameter(betreuungsArtParam, familyPortalSet)
			.setParameter(statusParam, InstitutionStatus.AKTIV)
			.getResultList();
	}

	@Nonnull
	public List<InstitutionDTO> get(@Nonnull Set<String> institutionIds) {
		if (institutionIds.isEmpty()) {
			return Collections.emptyList();
		}

		TypedQuery<InstitutionDTO> q = getInstitutionDTOTypedQuery(institutionIds);

		List<InstitutionDTO> resultList = q.getResultList();

		return resultList;
	}

	@Nonnull
	private TypedQuery<InstitutionDTO> getInstitutionDTOTypedQuery(
		@Nonnull Set<String> institutionIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<InstitutionDTO> query = cb.createQuery(InstitutionDTO.class);
		Root<Institution> root = query.from(Institution.class);
		Path<KontaktAngaben> adresse = root.get(Institution_.kontaktAdresse);

		query.select(cb.construct(
			InstitutionDTO.class,
			root.get(Institution_.id),
			root.get(Institution_.name),
			root.get(Institution_.traegerschaft),
			adresse.get(KontaktAngaben_.strasse),
			adresse.get(KontaktAngaben_.hausnummer),
			adresse.get(KontaktAngaben_.adresszusatz),
			adresse.get(KontaktAngaben_.plz),
			adresse.get(KontaktAngaben_.ort),
			adresse.get(KontaktAngaben_.land),
			root.get(Institution_.mandant)
		));

		//noinspection rawtypes
		ParameterExpression<Set> idsParam = cb.parameter(Set.class, "ids");
		Predicate idPredicate = root.get(Institution_.id).in(idsParam);

		query.where(idPredicate);

		return em.createQuery(query)
			.setParameter(idsParam, institutionIds);
	}

	@Nonnull
	public InstitutionDTO get(@Nonnull String institutionId) {

		TypedQuery<InstitutionDTO> q = getInstitutionDTOTypedQuery(Collections.singleton(institutionId));

		return q.getSingleResult();
	}

	@Nonnull
	public Optional<ClientInstitutionDTO> find(@Nonnull Client client) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClientInstitutionDTO> query = cb.createQuery(ClientInstitutionDTO.class);
		Root<Institution> root = query.from(Institution.class);
		Path<KontaktAngaben> adresse = root.get(Institution_.kontaktAdresse);

		query.select(cb.construct(
			ClientInstitutionDTO.class,
			root.get(Institution_.id),
			root.get(Institution_.name),
			root.get(Institution_.traegerschaft),
			adresse.get(KontaktAngaben_.strasse),
			adresse.get(KontaktAngaben_.hausnummer),
			adresse.get(KontaktAngaben_.adresszusatz),
			adresse.get(KontaktAngaben_.plz),
			adresse.get(KontaktAngaben_.ort),
			adresse.get(KontaktAngaben_.land),
			root.get(Institution_.mandant)
		));

		ParameterExpression<String> idParam = cb.parameter(String.class, Institution_.ID);
		Predicate idPredicate = cb.equal(root.get(Institution_.id), idParam);

		query.where(idPredicate);

		Optional<ClientInstitutionDTO> result = em.createQuery(query)
			.setParameter(idParam, client.getId().getInstitutionId())
			.getResultStream()
			.findAny();

		result.map(ClientInstitutionDTO::getClientBerechtigung).ifPresent(berechtigung -> {
			berechtigung.setVon(client.getGueltigAb());
			berechtigung.setBis(client.getGueltigBis());
		});

		return result;
	}

	@Nonnull
	public List<Institution> getAllForDashboard(
		@Nullable Long afterId,
		@Nullable Integer limit,
		@Nonnull KibonMandant mandant) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Institution> query = cb.createQuery(Institution.class);
		Root<Institution> root = query.from(Institution.class);

		//noinspection rawtypes
		ParameterExpression<Set> betreuungsArtParam = cb.parameter(Set.class, "betreuungsArtParam");
		Predicate betreuungArtPredicate = root.get(Institution_.betreuungsArt).in(betreuungsArtParam);

		ParameterExpression<InstitutionStatus> statusParam = cb.parameter(InstitutionStatus.class, "statusParam");
		Predicate statusPredicate = cb.equal(root.get(Institution_.status), statusParam);

		Predicate mandantPredicate = cb.equal(root.get(Institution_.mandant), mandant);

		Predicate unbekannteInsti = root.get(Institution_.id)
			.in(ConstantsUtil.ALL_UNKNOWN_BE_INSTITUTION_IDS)
			.not();

		if (afterId != null) {
			Predicate afterIdPredicate = cb.greaterThan(root.get(Institution_.sequenceId), afterId);
			query.where(betreuungArtPredicate, statusPredicate, mandantPredicate, unbekannteInsti, afterIdPredicate);
		} else {
			query.where(betreuungArtPredicate, statusPredicate, mandantPredicate, unbekannteInsti);
		}

		query.orderBy(cb.asc(root.get(Institution_.sequenceId)));

		Set<BetreuungsangebotTyp> dashboardSet =
			EnumSet.of(BetreuungsangebotTyp.KITA, BetreuungsangebotTyp.TAGESFAMILIEN);

		TypedQuery<Institution> q = em.createQuery(query);

		if (limit != null) {
			q.setMaxResults(limit);
		}

		return q.setParameter(betreuungsArtParam, dashboardSet)
			.setParameter(statusParam, InstitutionStatus.AKTIV)
			.getResultList();

	}
}
