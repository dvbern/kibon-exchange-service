package ch.dvbern.kibon.institution.service;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.model.Adresse;
import ch.dvbern.kibon.institution.model.Institution;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class InstitutionService {

	@Inject
	EntityManager em;

	@Inject
	ObjectMapper mapper;

	@Transactional(TxType.MANDATORY)
	public void institutionChanged(@Nonnull InstitutionEventDTO dto) {

		Institution institution = em.find(Institution.class, dto.getId());
		if (institution == null) {
			Institution newInstitution = mapper.convertValue(dto, Institution.class);

			em.persist(newInstitution);
		} else {
			// update
			institution.setName(dto.getName());
			institution.setTraegerschaft(dto.getTraegerschaft());

			Adresse adresse = institution.getAdresse();
			adresse.setStrasse(dto.getAdresse().getStrasse());
			adresse.setHausnummer(dto.getAdresse().getHausnummer());
			adresse.setAdresszusatz(dto.getAdresse().getAdresszusatz());
			adresse.setOrt(dto.getAdresse().getOrt());
			adresse.setPlz(dto.getAdresse().getPlz());
			adresse.setLand(dto.getAdresse().getLand());

			em.merge(institution);
		}
	}
}
