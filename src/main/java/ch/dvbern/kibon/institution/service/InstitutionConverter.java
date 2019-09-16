package ch.dvbern.kibon.institution.service;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.institution.AdresseDTO;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.model.Adresse;
import ch.dvbern.kibon.institution.model.Institution;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class InstitutionConverter {

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	ObjectMapper mapper;

	@Nonnull
	public Institution create(@Nonnull InstitutionEventDTO dto) {
		return mapper.convertValue(dto, Institution.class);
	}

	public void update(@Nonnull Institution institution, @Nonnull InstitutionEventDTO dto) {
		institution.setName(dto.getName());
		institution.setTraegerschaft(dto.getTraegerschaft());

		update(institution.getAdresse(), dto.getAdresse());
	}

	private void update(@Nonnull Adresse adresse, @Nonnull AdresseDTO dto) {
		adresse.setStrasse(dto.getStrasse());
		adresse.setHausnummer(dto.getHausnummer());
		adresse.setAdresszusatz(dto.getAdresszusatz());
		adresse.setOrt(dto.getOrt());
		adresse.setPlz(dto.getPlz());
		adresse.setLand(dto.getLand());
	}
}
