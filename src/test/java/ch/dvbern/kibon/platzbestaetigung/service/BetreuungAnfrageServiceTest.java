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

package ch.dvbern.kibon.platzbestaetigung.service;

import javax.persistence.EntityManager;

import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungAnfrageEventDTO;
import ch.dvbern.kibon.platzbestaetigung.model.BetreuungAnfrage;
import org.easymock.EasyMockExtension;
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
class BetreuungAnfrageServiceTest {

	@TestSubject
	private final BetreuungAnfrageService service = new BetreuungAnfrageService();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private EntityManager em;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private BetreuungAnfrageConverter converter;

	@Test
	void testOnBetreuungAnfrageCreated() {
		BetreuungAnfrageEventDTO dto = new BetreuungAnfrageEventDTO();

		BetreuungAnfrage betreuungAnfrage = new BetreuungAnfrage();
		expect(converter.create(dto)).andReturn(betreuungAnfrage);

		em.persist(betreuungAnfrage);
		expectLastCall();

		replay(em, converter);

		service.onBetreuungAnfrageCreated(dto);

		verify(em, converter);
	}
}
