package ch.dvbern.kibon.betreuung.service;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.betreuung.model.BetreuungStornierungAnfrage;
import ch.dvbern.kibon.betreuung.model.BetreuungStornierungAnfrageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class BetreuungStornierungAnfrageConverter {
	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper mapper;

	@Nonnull
	public BetreuungStornierungAnfrage create(@Nonnull BetreuungStornierungAnfrageDTO dto, @Nonnull LocalDateTime eventTimestamp) {
		BetreuungStornierungAnfrage betreuungAnfrage = new BetreuungStornierungAnfrage();
		betreuungAnfrage.setRefnr(dto.getFallNummer());
		betreuungAnfrage.setInstitutionId(dto.getInstitutionId());
		betreuungAnfrage.setEventTimestamp(eventTimestamp);

		return betreuungAnfrage;
	}
}
