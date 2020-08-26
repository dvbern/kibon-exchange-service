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
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.exchange.api.common.institution.InstitutionDTO;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.model.Adresse;
import ch.dvbern.kibon.institution.model.Adresse_;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.model.Institution_;

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
		Path<Adresse> adresse = root.get(Institution_.adresse);

		query.select(cb.construct(
			InstitutionDTO.class,
			root.get(Institution_.id),
			root.get(Institution_.name),
			root.get(Institution_.traegerschaft),
			adresse.get(Adresse_.strasse),
			adresse.get(Adresse_.hausnummer),
			adresse.get(Adresse_.adresszusatz),
			adresse.get(Adresse_.plz),
			adresse.get(Adresse_.ort),
			adresse.get(Adresse_.land)
		));

		//noinspection rawtypes
		ParameterExpression<Set> idsParam = cb.parameter(Set.class, "ids");
		Predicate idPredicate = root.get(Institution_.id).in(idsParam);

		query.where(idPredicate);

		return em.createQuery(query)
			.setParameter(idsParam, institutionIds);
	}

	@Nonnull
	public Response get(
		@Nonnull String institutionId,
		@Nonnull String clientName) {
		if (institutionId.isBlank()) {
			return Response.status(Status.NOT_FOUND).build();
		}

		Client client = em.find(Client.class, new ClientId(clientName, institutionId));

		if (client == null) {
			// Institution not found for given client
			return Response.status(Status.NOT_FOUND).build();
		}

		if(!client.getActive()) {
			// Client not active (forbidden) for given institution
			return Response.status(Status.FORBIDDEN).build();
		}

		TypedQuery<InstitutionDTO> q = getInstitutionDTOTypedQuery(Collections.singleton(institutionId));
		InstitutionDTO result;

		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
			// Institution does not exist
			// It should not happen
			return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity("IntitutionDTO not found for ID: " + institutionId)
				.build();
		}

		return Response.ok(result).build();
	}
}
