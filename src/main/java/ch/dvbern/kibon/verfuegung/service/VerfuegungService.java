package ch.dvbern.kibon.verfuegung.service;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung_;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.model.Verfuegung_;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class VerfuegungService {

	@Inject
	EntityManager em;

	@Inject
	ObjectMapper mapper;

	@PostConstruct
	public void init() throws IOException {
		// strange, but if I don't perform deserialisation manually, vladmihalcea's ObjectMapperWrapper throws a NPE.
		mapper.readTree("{}");
	}

	@Transactional(TxType.MANDATORY)
	public void verfuegungCreated(@Nonnull VerfuegungEventDTO dto) {
		Verfuegung verfuegung = mapper.convertValue(dto, Verfuegung.class);

		em.persist(verfuegung);
	}

	@Transactional(TxType.MANDATORY)
	public List<ClientVerfuegungDTO> getAllForClient(@Nonnull String clientId) {
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

		ParameterExpression<String> clientParam = cb.parameter(String.class, "clientId");
		Predicate clientPredicate = cb.equal(root.get(ClientVerfuegung_.clientId), clientParam);

		query
			.where(clientPredicate)
			.orderBy(cb.asc(root.get(ClientVerfuegung_.since)), cb.asc(root.get(ClientVerfuegung_.id)));

		List<ClientVerfuegungDTO> resultList = em.createQuery(query)
			.setParameter(clientParam, clientId)
			.getResultList();

		return resultList;
	}

	@Transactional(TxType.MANDATORY)
	public List<Verfuegung> getAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Verfuegung> query = cb.createQuery(Verfuegung.class);
		query.from(Verfuegung.class);

		List<Verfuegung> resultList = em.createQuery(query)
			.getResultList();

		return resultList;
	}
}
