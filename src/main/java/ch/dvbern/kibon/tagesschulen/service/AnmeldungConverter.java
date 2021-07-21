package ch.dvbern.kibon.tagesschulen.service;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import ch.dvbern.kibon.betreuung.model.BetreuungAnfrage;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungAnfrageEventDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.types.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.types.KindDTO;
import ch.dvbern.kibon.shared.model.Gesuchsperiode;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.AnmeldungModul;
import ch.dvbern.kibon.tagesschulen.model.Modul;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class AnmeldungConverter {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper mapper;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@Nonnull
	public Anmeldung create(@Nonnull TagesschuleAnmeldungEventDTO dto, @Nonnull LocalDateTime eventTimestamp) {
		Anmeldung anmeldung = new Anmeldung();
		anmeldung.setInstitutionId(dto.getInstitutionId());
		anmeldung.setRefnr(dto.getAnmeldungsDetails().getRefnr());
		anmeldung.setAnmeldungZurueckgezogen(dto.getAnmeldungZurueckgezogen());
		anmeldung.setAbholung(dto.getAnmeldungsDetails().getAbholung().name());
		anmeldung.setBemerkung(dto.getAnmeldungsDetails().getBemerkung());
		anmeldung.setEintrittsdatum(dto.getAnmeldungsDetails().getEintrittsdatum());
		anmeldung.setEventTimestamp(eventTimestamp);
		anmeldung.setGesuchsteller(toGesuchsteller(dto.getAntragstellendePerson()));
		anmeldung.setKind(toKind(dto.getKind()));
		anmeldung.setAbweichungZweitesSemester(dto.getAnmeldungsDetails().getAbweichungZweitesSemester());
		anmeldung.setFreigegebenAm(dto.getFreigegebenAm());
		anmeldung.setPlanKlasse(dto.getAnmeldungsDetails().getPlanKlasse());

		Gesuchsperiode gesuchsperiode = em.find(Gesuchsperiode.class, dto.getGesuchsperiode().getId());
		anmeldung.setGesuchsperiode(gesuchsperiode);
		dto.getAnmeldungsDetails().getModulSelection().forEach(
			modulAuswahlDTO -> {
				AnmeldungModul anmeldungModul = new AnmeldungModul();
				anmeldungModul.setAnmeldung(anmeldung);
				anmeldungModul.setIntervall(modulAuswahlDTO.getIntervall().name());
				anmeldungModul.setWeekday(modulAuswahlDTO.getWeekday());
				anmeldungModul.setModul(em.find(Modul.class, modulAuswahlDTO.getModulId()));
				anmeldung.getAnmeldungModulSet().add(anmeldungModul);
			}
		);

		return anmeldung;
	}



	@Nonnull
	private ObjectNode toKind(@Nonnull KindDTO kind) {
		return mapper.createObjectNode()
			.put("vorname", kind.getVorname())
			.put("nachname", kind.getNachname())
			.put("geburtsdatum", kind.getGeburtsdatum().toString());
	}

	@Nonnull
	private ObjectNode toGesuchsteller(@Nonnull GesuchstellerDTO gesuchsteller) {
		return mapper.createObjectNode()
			.put("vorname", gesuchsteller.getVorname())
			.put("nachname", gesuchsteller.getNachname())
			.put("email", gesuchsteller.getEmail());
	}

}
