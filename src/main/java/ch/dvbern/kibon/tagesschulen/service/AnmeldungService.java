package ch.dvbern.kibon.tagesschulen.service;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung_;

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

}
