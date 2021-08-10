package ch.dvbern.kibon.tagesschulen.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.types.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.types.KindDTO;
import ch.dvbern.kibon.shared.model.Gesuchsperiode;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.AnmeldungModul;
import ch.dvbern.kibon.tagesschulen.model.AnmeldungModulDTO;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldungDTO;
import ch.dvbern.kibon.tagesschulen.model.Modul;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AnmeldungConverter {

	private static final Logger LOG = LoggerFactory.getLogger(AnmeldungConverter.class);

	private static final int SUNDAY_INT_VALUE = 0;
	private static final int MONDAY_INT_VALUE = 1;
	private static final int TUESDAY_INT_VALUE = 2;
	private static final int WEDNESDAY_INT_VALUE = 3;
	private static final int THURSDAY_INT_VALUE = 4;
	private static final int FRIDAY_INT_VALUE = 5;
	private static final int SATURDAY_INT_VALUE = 6;

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
		anmeldung.setAbholung(dto.getAnmeldungsDetails().getAbholung());
		anmeldung.setBemerkung(dto.getAnmeldungsDetails().getBemerkung());
		anmeldung.setEintrittsdatum(dto.getAnmeldungsDetails().getEintrittsdatum());
		anmeldung.setEventTimestamp(eventTimestamp);
		anmeldung.setGesuchsteller(toGesuchsteller(dto.getAntragstellendePerson()));
		anmeldung.setKind(toKind(dto.getKind()));
		anmeldung.setAbweichungZweitesSemester(dto.getAnmeldungsDetails().getAbweichungZweitesSemester());
		anmeldung.setFreigegebenAm(dto.getFreigegebenAm());
		anmeldung.setPlanKlasse(dto.getAnmeldungsDetails().getPlanKlasse());
		anmeldung.setStatus(dto.getStatus());
		anmeldung.setAnmeldungZurueckgezogen(dto.getAnmeldungZurueckgezogen());
		anmeldung.setVersion(dto.getVersion());

		Gesuchsperiode gesuchsperiode = em.find(Gesuchsperiode.class, dto.getGesuchsperiode().getId());
		anmeldung.setGesuchsperiode(gesuchsperiode);
		dto.getAnmeldungsDetails().getModulSelection().forEach(
			modulAuswahlDTO -> {
				AnmeldungModul anmeldungModul = new AnmeldungModul();
				anmeldungModul.setAnmeldung(anmeldung);
				anmeldungModul.setIntervall(modulAuswahlDTO.getIntervall());
				anmeldungModul.setWeekday(modulAuswahlDTO.getWeekday());
				Modul modul = em.find(Modul.class, modulAuswahlDTO.getModulId());
				if (modul != null) {
					anmeldungModul.setModul(modul);
					anmeldung.getAnmeldungModulSet().add(anmeldungModul);
				} else {
					LOG.warn("Modul mit ID: {}  war nicht gefunden!", modulAuswahlDTO.getModulId());
				}
			}
		);
		return anmeldung;
	}

	@Nonnull
	private ObjectNode toKind(@Nonnull KindDTO kind) {
		return mapper.createObjectNode()
			.put("vorname", kind.getVorname())
			.put("nachname", kind.getNachname())
			.put("geburtsdatum", kind.getGeburtsdatum().toString())
			.put("geschlecht", kind.getGeschlecht().name());
	}

	@Nonnull
	private ObjectNode toGesuchsteller(@Nonnull GesuchstellerDTO gesuchsteller) {
		ObjectNode result = mapper.createObjectNode()
			.put("vorname", gesuchsteller.getVorname())
			.put("nachname", gesuchsteller.getNachname())
			.put("geburtsdatum", gesuchsteller.getGeburtsdatum().toString())
			.put("geschlecht", gesuchsteller.getGeschlecht().name())
			.put("email", gesuchsteller.getEmail());

		result.putObject("adresse")
			.put("ort", gesuchsteller.getAdresse().getOrt())
			.put("land", gesuchsteller.getAdresse().getLand())
			.put("strasse", gesuchsteller.getAdresse().getStrasse())
			.put("hausnummer", gesuchsteller.getAdresse().getHausnummer())
			.put("adresszusatz", gesuchsteller.getAdresse().getAdresszusatz())
			.put("plz", gesuchsteller.getAdresse().getPlz());

		return result;
	}

	public ClientAnmeldungDTO toClientAnmeldungDTO(ClientAnmeldung clientAnmeldung) {
		Anmeldung anmeldung = clientAnmeldung.getAnmeldung();
		ClientAnmeldungDTO clientAnmeldungDTO = new ClientAnmeldungDTO(
			clientAnmeldung.getId(),
			anmeldung.getInstitutionId(),
			anmeldung.getRefnr(),
			anmeldung.getVersion(),
			anmeldung.getEventTimestamp(),
			anmeldung.getGesuchsperiode().getGueltigAb(),
			anmeldung.getGesuchsperiode().getGueltigBis(),
			anmeldung.getKind(),
			anmeldung.getGesuchsteller(),
			anmeldung.getPlanKlasse(),
			anmeldung.getAbholung(),
			anmeldung.getAbweichungZweitesSemester(),
			anmeldung.getBemerkung(),
			anmeldung.getAnmeldungZurueckgezogen(),
			anmeldung.getEintrittsdatum(),
			anmeldung.getAnmeldungModulSet().stream().map(this::toAnmeldungModul).collect(Collectors.toList())
		);
		return clientAnmeldungDTO;
	}
	private AnmeldungModulDTO toAnmeldungModul(AnmeldungModul anmeldungModul) {
		return new AnmeldungModulDTO(
			anmeldungModul.getModul().getId(),
			toDayOfWeek(anmeldungModul.getWeekday()),
			anmeldungModul.getIntervall()
		);
	}

	private DayOfWeek toDayOfWeek(int weekday) {
		switch (weekday) {
		case SUNDAY_INT_VALUE:
			return DayOfWeek.SUNDAY;
		case MONDAY_INT_VALUE:
			return DayOfWeek.MONDAY;
		case TUESDAY_INT_VALUE:
			return DayOfWeek.TUESDAY;
		case WEDNESDAY_INT_VALUE:
			return DayOfWeek.WEDNESDAY;
		case THURSDAY_INT_VALUE:
			return DayOfWeek.THURSDAY;
		case FRIDAY_INT_VALUE:
			return DayOfWeek.FRIDAY;
		case SATURDAY_INT_VALUE:
		default:
			return DayOfWeek.SATURDAY;
		}
	}
}
