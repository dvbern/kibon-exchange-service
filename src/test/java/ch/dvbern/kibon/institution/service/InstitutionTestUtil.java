package ch.dvbern.kibon.institution.service;

import java.util.Comparator;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.institution.AdresseDTO;
import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.model.Adresse;
import ch.dvbern.kibon.institution.model.Institution;
import com.github.javafaker.Faker;

public final class InstitutionTestUtil {

	public static final Comparator<Adresse> ADRESSE_COMPARATOR = Comparator
		.comparing(Adresse::getStrasse)
		.thenComparing(Adresse::getHausnummer, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(Adresse::getAdresszusatz, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(Adresse::getPlz)
		.thenComparing(Adresse::getOrt)
		.thenComparing(Adresse::getLand);

	public static final Comparator<Institution> INSTITUTION_COMPARATOR = Comparator
		.comparing(Institution::getName)
		.thenComparing(Institution::getTraegerschaft, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(Institution::getAdresse, ADRESSE_COMPARATOR);

	private InstitutionTestUtil() {
		// util
	}

	@Nonnull
	public static InstitutionEventDTO createInstitutionEvent() {
		Faker faker = new Faker();

		AdresseDTO adresse = new AdresseDTO(
			faker.address().streetName(),
			faker.address().buildingNumber(),
			null,
			faker.address().zipCode(),
			faker.address().cityName(),
			faker.address().countryCode());

		return new InstitutionEventDTO("99", faker.funnyName().name(), faker.funnyName().name(), adresse);
	}

	@Nonnull
	public static Institution fromDTO(@Nonnull InstitutionEventDTO dto) {
		Institution institution = new Institution();
		InstitutionConverter converter = new InstitutionConverter();

		converter.update(institution, dto);

		return institution;
	}
}
