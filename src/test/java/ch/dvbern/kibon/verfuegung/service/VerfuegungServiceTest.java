/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.verfuegung.service;

import javax.persistence.EntityManager;

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.testutils.EasyMockExtension;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

@ExtendWith(EasyMockExtension.class)
class VerfuegungServiceTest {

	@TestSubject
	private final VerfuegungService service = new VerfuegungService();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private EntityManager em;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private VerfuegungConverter converter;

	@Test
	public void testOnVerfuegungCreated() {
		VerfuegungEventDTO dto = new VerfuegungEventDTO();

		Verfuegung verfuegung = new Verfuegung();
		expect(converter.create(dto)).andReturn(verfuegung);

		em.persist(verfuegung);
		expectLastCall();

		replay(em, converter);

		service.onVerfuegungCreated(dto);

		verify(em, converter);
	}
}
