/*
 * Copyright (C) 2024 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.dvbern.kibon.api;

import ch.dvbern.kibon.exchange.api.common.betreuung.BetreuungsAngebot;
import ch.dvbern.kibon.exchange.api.common.institution.KibonMandant;
import ch.dvbern.kibon.exchange.api.common.shared.BetreuungsangebotTyp;
import ch.dvbern.kibon.exchange.api.common.shared.EinschulungTyp;
import ch.dvbern.kibon.exchange.api.common.shared.Zeiteinheit;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.AbholungTagesschule;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.Geschlecht;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.Intervall;
import ch.dvbern.kibon.exchange.api.common.tagesschule.anmeldung.TagesschuleAnmeldungStatus;
import ch.dvbern.kibon.exchange.api.common.verfuegung.Regelwerk;
import ch.dvbern.kibon.exchange.commons.types.Mandant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ApiCompatiblitityTest {

	static Stream<Arguments> enums() {
		return Stream.of(
			Arguments.of(Mandant.class, KibonMandant.class),
			Arguments.of(ch.dvbern.kibon.exchange.commons.types.BetreuungsangebotTyp.class, BetreuungsangebotTyp.class),
			Arguments.of(ch.dvbern.kibon.exchange.commons.types.EinschulungTyp.class, EinschulungTyp.class),
			Arguments.of(ch.dvbern.kibon.exchange.commons.types.Zeiteinheit.class, Zeiteinheit.class),
			Arguments.of(ch.dvbern.kibon.exchange.commons.tagesschulen.AbholungTagesschule.class, AbholungTagesschule.class),
			Arguments.of(ch.dvbern.kibon.exchange.commons.types.Geschlecht.class, Geschlecht.class),
			Arguments.of(ch.dvbern.kibon.exchange.commons.types.Intervall.class, Intervall.class),
			Arguments.of(ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungStatus.class, TagesschuleAnmeldungStatus.class),
			Arguments.of(ch.dvbern.kibon.exchange.commons.types.Regelwerk.class, Regelwerk.class)
		);
	}

	@ParameterizedTest
	@MethodSource("enums")
	<E extends Enum<E>> void testSetupValidation(Class<E> exchangeEnum, Class<E> apiEnum) {
		assertThat(exchangeEnum, is(not(apiEnum)));
	}

	@ParameterizedTest
	@MethodSource("enums")
	<E extends Enum<E>> void testEnumMapping(Class<E> exchangeEnum, Class<E> apiEnum) {
		Set<String> apiNames = names(apiEnum);
		Set<String> exchangeNames = names(exchangeEnum);

		assertThat(apiNames, containsInAnyOrder(exchangeNames.toArray()));
	}

	@Test
	void tagesschuleBetreuungsAngebot() {
		Set<String> apiNames = names(BetreuungsAngebot.class);
		Set<String> exchangeNames = names(BetreuungsangebotTyp.class);

		assertThat(apiNames, hasItems(exchangeNames.toArray(String[]::new)));
	}

	@Nonnull
	private <E extends Enum<E>> Set<String> names(Class<E> enumClass) {
		return Arrays.stream(enumClass.getEnumConstants())
			.map(Enum::name)
			// we use UNKNOWN as default value in the AVRO schemas - UNKNOWN should not be exposed in the API
			.filter(name -> !name.equals("UNKNOWN"))
			.collect(Collectors.toSet());
	}
}
