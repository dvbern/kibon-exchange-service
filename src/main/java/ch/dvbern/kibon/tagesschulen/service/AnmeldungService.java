package ch.dvbern.kibon.tagesschulen.service;

import java.time.LocalDateTime;
import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;

@ApplicationScoped
public class AnmeldungService {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	AnmeldungConverter converter;

	public static final Comparator<Anmeldung> ANMELDUNG_COMPARATOR = Comparator
		.comparing(Anmeldung::getRefnr)
		.thenComparing(Anmeldung::getBemerkung)
		.thenComparing(Anmeldung::getAbholung)
		.thenComparing(Anmeldung::getStatus)
		.thenComparing(Anmeldung::getEintrittsdatum)
		.thenComparing(Anmeldung::getFreigegebenAm);



	@Transactional(TxType.MANDATORY)
	public void onAnmeldungTagesschule(@Nonnull TagesschuleAnmeldungEventDTO dto, @Nonnull LocalDateTime eventTime) {
		//sucht ob es schon eine Anmeldung mit selbe Refnummer gibt
		Anmeldung lastExistingAnmeldung = getLatestAnmeldung(dto.getAnmeldungsDetails().getRefnr());
		Anmeldung newAnmeldung = converter.create(dto, eventTime);

		if (ANMELDUNG_COMPARATOR.compare(lastExistingAnmeldung, newAnmeldung) == 0) {
			// ignore new
		} else {
			em.persist(dto);
		}


	}

	private Anmeldung getLatestAnmeldung(String refnr) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Anmeldung> query = cb.createQuery(Anmeldung.class);
		Root<Anmeldung> root = query.from(Anmeldung.class);
		return null;
	}

}
