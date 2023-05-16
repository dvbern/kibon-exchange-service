/*
 * Copyright (C) 2020 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.api.verfuegung;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId;
import ch.dvbern.kibon.clients.service.ClientService;
import ch.dvbern.kibon.exchange.api.common.verfuegung.VerfuegungDTO;
import ch.dvbern.kibon.exchange.api.common.verfuegung.VerfuegungenDTO;
import ch.dvbern.kibon.exchange.api.common.verfuegung.ZeitabschnittDTO;
import org.easymock.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(EasyMockExtension.class)
class VerfuegungenResourceTest extends EasyMockSupport {

	private static final String INSTITUTION_ID = "1";
	private static final String CLIENT_NAME = "fake-client";

	@TestSubject
	private final VerfuegungenResource resource = new VerfuegungenResource();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private ClientService clientService;

	@Test
	void noFilteringWhenNoGueltigkeitRestriction() {
		VerfuegungenDTO dto = createDTO();
		VerfuegungDTO verfuegung = dto.getVerfuegungen().get(0);

		ZeitabschnittDTO z1 = createZeitabschnitt(YearMonth.of(2021, 1));
		ZeitabschnittDTO z2 = createZeitabschnitt(YearMonth.of(2021, 2));
		List<ZeitabschnittDTO> zeitabschnitte = Arrays.asList(z1, z2);
		verfuegung.setZeitabschnitte(zeitabschnitte);

		Client client = createClient(null, null);

		expect(clientService.get(client.getId()))
			.andReturn(client);

		replayAll();

		resource.removeZeitabschnitteOutsideGueltigkeit(CLIENT_NAME, dto, Collections.singleton(INSTITUTION_ID));

		assertThat(verfuegung.getZeitabschnitte(), is(zeitabschnitte));

		verifyAll();
	}

	@Test
	void removesZeitabschnittBeforeClientGueltigAb() {
		VerfuegungenDTO dto = createDTO();
		VerfuegungDTO verfuegung = dto.getVerfuegungen().get(0);

		ZeitabschnittDTO z1 = createZeitabschnitt(YearMonth.of(2021, 1));
		ZeitabschnittDTO z2 = createZeitabschnitt(YearMonth.of(2021, 2));
		List<ZeitabschnittDTO> zeitabschnitte = Arrays.asList(z1, z2);
		verfuegung.getZeitabschnitte().addAll(zeitabschnitte);

		Client client = createClient(LocalDate.of(2021, 2, 1), null);

		expect(clientService.get(client.getId()))
			.andReturn(client);

		replayAll();

		resource.removeZeitabschnitteOutsideGueltigkeit(CLIENT_NAME, dto, Collections.singleton(INSTITUTION_ID));

		assertThat(verfuegung.getZeitabschnitte(), contains(z2));

		verifyAll();
	}

	@Test
	void removesZeitabschnittAfterClientGueltigBis() {
		VerfuegungenDTO dto = createDTO();
		VerfuegungDTO verfuegung = dto.getVerfuegungen().get(0);

		ZeitabschnittDTO z1 = createZeitabschnitt(YearMonth.of(2021, 1));
		ZeitabschnittDTO z2 = createZeitabschnitt(YearMonth.of(2021, 2));
		List<ZeitabschnittDTO> zeitabschnitte = Arrays.asList(z1, z2);
		verfuegung.getZeitabschnitte().addAll(zeitabschnitte);

		Client client = createClient(null, LocalDate.of(2021, 1, 31));

		expect(clientService.get(client.getId()))
			.andReturn(client);

		replayAll();

		resource.removeZeitabschnitteOutsideGueltigkeit(CLIENT_NAME, dto, Collections.singleton(INSTITUTION_ID));

		assertThat(verfuegung.getZeitabschnitte(), contains(z1));

		verifyAll();
	}

	@Test
	void removesIgnorierteZeitabschnitt() {
		VerfuegungenDTO dto = createDTO();
		VerfuegungDTO verfuegung = dto.getVerfuegungen().get(0);
		YearMonth gueltigkeitsRange = YearMonth.of(2021, 2);

		ZeitabschnittDTO z1 = createZeitabschnitt(YearMonth.of(2021, 1));
		ZeitabschnittDTO z2 = createZeitabschnitt(gueltigkeitsRange);
		ZeitabschnittDTO z3 = createZeitabschnitt(YearMonth.of(2021, 3));
		List<ZeitabschnittDTO> zeitabschnitte = Arrays.asList(z1, z2, z3);
		verfuegung.getIgnorierteZeitabschnitte().addAll(zeitabschnitte);

		Client client = createClient(gueltigkeitsRange.atDay(1), gueltigkeitsRange.atEndOfMonth());

		expect(clientService.get(client.getId()))
			.andReturn(client);

		replayAll();

		resource.removeZeitabschnitteOutsideGueltigkeit(CLIENT_NAME, dto, Collections.singleton(INSTITUTION_ID));

		assertThat(verfuegung.getIgnorierteZeitabschnitte(), contains(z2));

		verifyAll();
	}

	@Test
	void removesVerfuegungWithoutZeitabschnitte() {
		VerfuegungenDTO dto = createDTO();
		VerfuegungDTO verfuegung = dto.getVerfuegungen().get(0);

		ZeitabschnittDTO z1 = createZeitabschnitt(YearMonth.of(2021, 1));
		verfuegung.getZeitabschnitte().add(z1);

		Client client = createClient(LocalDate.of(2021, 2, 1), null);

		expect(clientService.get(client.getId()))
			.andReturn(client);

		replayAll();

		resource.removeZeitabschnitteOutsideGueltigkeit(CLIENT_NAME, dto, Collections.singleton(INSTITUTION_ID));

		assertThat(dto.getVerfuegungen(), not(hasItem(verfuegung)));

		verifyAll();
	}

	@Nonnull
	private VerfuegungenDTO createDTO() {
		VerfuegungenDTO verfuegungenDTO = new VerfuegungenDTO();
		VerfuegungDTO dto = new VerfuegungDTO();
		dto.setInstitutionId(INSTITUTION_ID);

		verfuegungenDTO.getVerfuegungen().add(dto);

		return verfuegungenDTO;
	}

	@Nonnull
	private ZeitabschnittDTO createZeitabschnitt(@Nonnull YearMonth yearMonth) {
		ZeitabschnittDTO zeitabschnitt = new ZeitabschnittDTO();
		zeitabschnitt.setVon(yearMonth.atDay(1));
		zeitabschnitt.setBis(yearMonth.atEndOfMonth());

		return zeitabschnitt;
	}

	@Nonnull
	private Client createClient(@Nullable LocalDate gueltigAb, @Nullable LocalDate gueltigBis) {
		ClientId id = new ClientId(CLIENT_NAME, INSTITUTION_ID);

		return new Client(id, LocalDateTime.now(), gueltigAb, gueltigBis);
	}
}
