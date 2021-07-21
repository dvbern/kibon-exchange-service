package ch.dvbern.kibon.tagesschulen.service;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;

@ApplicationScoped
public class AnmeldungService {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	AnmeldungConverter converter;


	@Transactional(TxType.MANDATORY)
	public void onAnmeldungTagesschule(@Nonnull TagesschuleAnmeldungEventDTO dto) {
		//sucht ob es schon eine Anmeldung mit selbe Refnummer gibt

		//wenn ja comparator implementieren, kontrollieren alle Felder ausser Status, Id und timestamps und version




	}
}
