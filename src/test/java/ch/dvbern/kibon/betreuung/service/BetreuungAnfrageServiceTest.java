/*
 * Copyright (C) 2021 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.betreuung.service;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import ch.dvbern.kibon.betreuung.model.BetreuungAnfrage;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungAnfrageEventDTO;
import org.easymock.EasyMockExtension;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

@ExtendWith(EasyMockExtension.class)
class BetreuungAnfrageServiceTest extends EasyMockSupport {

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
		LocalDateTime eventTime = LocalDateTime.now();

		BetreuungAnfrage betreuungAnfrage = new BetreuungAnfrage();
		expect(converter.create(dto, eventTime)).andReturn(betreuungAnfrage);

		em.persist(betreuungAnfrage);
		expectLastCall();

		replayAll();

		service.onBetreuungAnfrageCreated(dto, eventTime);

		verifyAll();
	}
}
