package ch.dvbern.kibon.verfuegung.service;

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

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung_;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.model.Verfuegung_;
import ch.dvbern.kibon.verfuegung.service.filter.ClientVerfuegungFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service responsible for {@link Verfuegung} handling.<br>
 * Uses the {@link ClientVerfuegung} table for speedy filtering/searching.
 */
@ApplicationScoped
public class VerfuegungService {

	@Inject
	EntityManager em;

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	ObjectMapper mapper;

	/**
	 * Stores the verfuegung in response to the verfuegungCreated event.
	 */
	@Transactional(TxType.MANDATORY)
	public void onVerfuegungCreated(@Nonnull VerfuegungEventDTO dto) {
		Verfuegung verfuegung = mapper.convertValue(dto, Verfuegung.class);

		em.persist(verfuegung);
	}

	/**
	 * Delivers all {@link ClientVerfuegungDTO} for the given filter.
	 */
	@Transactional(TxType.MANDATORY)
	public List<ClientVerfuegungDTO> getAllForClient(@Nonnull ClientVerfuegungFilter filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClientVerfuegungDTO> query = cb.createQuery(ClientVerfuegungDTO.class);
		Root<ClientVerfuegung> root = query.from(ClientVerfuegung.class);
		Join<ClientVerfuegung, Verfuegung> verfuegung = root.join(ClientVerfuegung_.verfuegung);

		query.select(cb.construct(
			ClientVerfuegungDTO.class,
			root.get(ClientVerfuegung_.id),
			root.get(ClientVerfuegung_.since),
			verfuegung.get(Verfuegung_.refnr),
			verfuegung.get(Verfuegung_.institutionId),
			verfuegung.get(Verfuegung_.von),
			verfuegung.get(Verfuegung_.bis),
			verfuegung.get(Verfuegung_.version),
			verfuegung.get(Verfuegung_.verfuegtAm),
			verfuegung.get(Verfuegung_.betreuungsArt),
			verfuegung.get(Verfuegung_.kind),
			verfuegung.get(Verfuegung_.gesuchsteller),
			verfuegung.get(Verfuegung_.zeitabschnitte),
			verfuegung.get(Verfuegung_.ignorierteZeitabschnitte)
		));

		filter.setPredicate(query, root, cb);

		query.orderBy(cb.asc(root.get(ClientVerfuegung_.since)), cb.asc(root.get(ClientVerfuegung_.id)));

		TypedQuery<ClientVerfuegungDTO> q = em.createQuery(query);

		filter.setParameters(q);

		List<ClientVerfuegungDTO> resultList = q.getResultList();

		return resultList;
	}
}
