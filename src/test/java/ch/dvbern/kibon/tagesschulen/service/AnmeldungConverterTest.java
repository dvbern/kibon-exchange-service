package ch.dvbern.kibon.tagesschulen.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.exchange.commons.tagesschulen.ModulAuswahlDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.types.Gesuchsperiode;
import ch.dvbern.kibon.exchange.commons.types.GesuchstellerDTO;
import ch.dvbern.kibon.exchange.commons.types.KindDTO;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.Modul;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.hamcrest.pojo.IsPojo;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.spotify.hamcrest.jackson.JsonMatchers.jsonArray;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonInt;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonText;
import static com.spotify.hamcrest.pojo.IsPojo.pojo;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.MatcherAssert.assertThat;
import static ch.dvbern.kibon.tagesschulen.service.AnmeldungTagesschuleTestUtil.createTagesschuleAnmeldungTestDTO;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

@ExtendWith(EasyMockExtension.class)
public class AnmeldungConverterTest {

	@TestSubject
	private AnmeldungConverter converter = new AnmeldungConverter();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(MockType.NICE)
	private EntityManager em;

	@BeforeEach
	public void setup() {
		converter.mapper = new ObjectMapper();
	}

	@Test
	public void testCreate() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		replayAll(dto);
		Anmeldung anmeldung = converter.create(dto, LocalDateTime.now());
		assertThat(anmeldung, matchesDTO(dto));
	}

	private void replayAll(TagesschuleAnmeldungEventDTO dto) {
		ch.dvbern.kibon.shared.model.Gesuchsperiode gesuchsperiode = new ch.dvbern.kibon.shared.model.Gesuchsperiode();
		gesuchsperiode.setId(dto.getGesuchsperiode().getId());
		gesuchsperiode.setGueltigAb(dto.getGesuchsperiode().getGueltigAb());
		gesuchsperiode.setGueltigBis(dto.getGesuchsperiode().getGueltigBis());
		expect(em.find(ch.dvbern.kibon.shared.model.Gesuchsperiode.class, dto.getGesuchsperiode().getId())).andReturn(
			gesuchsperiode);
		dto.getAnmeldungsDetails().getModulSelection().forEach(
			modulAuswahlDTO -> expect(em.find(Modul.class, modulAuswahlDTO.getModulId())).andReturn(new Modul(
				modulAuswahlDTO.getModulId())));
		expectLastCall();
		replay(em);
	}

	@Nonnull
	private IsPojo<Anmeldung> matchesDTO(@Nonnull TagesschuleAnmeldungEventDTO dto) {
		return pojo(Anmeldung.class)
			.withProperty("kind", matchesKindDTO(dto.getKind()))
			.withProperty("gesuchsteller", matchesGesuchstellerDTO(dto.getAntragstellendePerson()))
			.withProperty("freigegebenAm", is(dto.getFreigegebenAm()))
			.withProperty("status", is(dto.getStatus()))
			.withProperty("anmeldungZurueckgezogen", is(dto.getAnmeldungZurueckgezogen()))
			.withProperty("refnr", is(dto.getAnmeldungsDetails().getRefnr()))
			.withProperty("eintrittsdatum", is(dto.getAnmeldungsDetails().getEintrittsdatum()))
			.withProperty("planKlasse", is(dto.getAnmeldungsDetails().getPlanKlasse()))
			.withProperty("abholung", is(dto.getAnmeldungsDetails().getAbholung()))
			.withProperty("abweichungZweitesSemester", is(dto.getAnmeldungsDetails().getAbweichungZweitesSemester()))
			.withProperty("bemerkung", is(dto.getAnmeldungsDetails().getBemerkung()))
			.withProperty("gesuchsperiode", matchesGesuchperiode(dto.getGesuchsperiode()))
			.withProperty("institutionId", is(dto.getInstitutionId()))
			.where(
				Anmeldung::getAnmeldungModule,
				is(jsonArray(containsInAnyOrder(toMatchers(dto.getAnmeldungsDetails().getModulSelection())))))
			;
	}

	private Collection<Matcher<? super JsonNode>> toMatchers(@Nonnull List<ModulAuswahlDTO> modulAuswahlDTOS) {
		return modulAuswahlDTOS.stream()
			.map(this::matchesAnmeldungModul)
			.collect(Collectors.toList());
	}

	private Matcher<JsonNode> matchesAnmeldungModul(ModulAuswahlDTO modulAuswahlDTO) {
		return is(jsonObject()
			.where("intervall", is(jsonText(modulAuswahlDTO.getIntervall().name())))
			.where("wochentag", is(jsonText(converter.toDayOfWeek(modulAuswahlDTO.getWeekday()).name())))
			.where("modulId", is(jsonText(modulAuswahlDTO.getModulId()))));
	}

	private Matcher<?> matchesGesuchperiode(Gesuchsperiode gesuchsperiode) {
		return is(pojo(ch.dvbern.kibon.shared.model.Gesuchsperiode.class)
			.withProperty("id", is(gesuchsperiode.getId()))
			.withProperty("gueltigAb", is(gesuchsperiode.getGueltigAb()))
			.withProperty("gueltigBis", is(gesuchsperiode.getGueltigBis()))
		);
	}

	@Nonnull
	private Matcher<JsonNode> matchesKindDTO(@Nonnull KindDTO kind) {
		return is(jsonObject()
			.where("vorname", is(jsonText(kind.getVorname())))
			.where("nachname", is(jsonText(kind.getNachname())))
			.where("geburtsdatum", is(jsonText(kind.getGeburtsdatum().toString()))));
	}

	@Nonnull
	private Matcher<JsonNode> matchesGesuchstellerDTO(@Nonnull GesuchstellerDTO gesuchsteller) {
		return is(jsonObject()
			.where("vorname", is(jsonText(gesuchsteller.getVorname())))
			.where("nachname", is(jsonText(gesuchsteller.getNachname())))
			.where("geburtsdatum", is(jsonText(gesuchsteller.getGeburtsdatum().toString())))
			.where("email", is(jsonText(gesuchsteller.getEmail())))
			.where("geschlecht", is(jsonText(gesuchsteller.getGeschlecht().name())))
			.where("adresse", is(jsonObject()
				.where("ort", is(jsonText(gesuchsteller.getAdresse().getOrt())))
				.where("land", is(jsonText(gesuchsteller.getAdresse().getLand())))
				.where("strasse", is(jsonText(gesuchsteller.getAdresse().getStrasse())))
				.where("hausnummer", is(jsonText(gesuchsteller.getAdresse().getHausnummer())))
				.where("adresszusatz", is(jsonText(gesuchsteller.getAdresse().getAdresszusatz())))
				.where("plz", is(jsonText(gesuchsteller.getAdresse().getPlz())))
			)));
	}
}
