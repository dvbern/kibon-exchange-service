package ch.dvbern.kibon.institution.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
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

import ch.dvbern.kibon.exchange.api.institution.model.InstitutionDTO;
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

	@Inject
	EntityManager em;

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

		TypedQuery<InstitutionDTO> q = em.createQuery(query)
			.setParameter(idsParam, institutionIds);

		List<InstitutionDTO> resultList = q.getResultList();

		return resultList;
	}
}
