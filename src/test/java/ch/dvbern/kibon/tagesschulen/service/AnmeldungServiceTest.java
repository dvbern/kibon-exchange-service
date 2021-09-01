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

package ch.dvbern.kibon.tagesschulen.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungDetailsDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldungDTO;
import ch.dvbern.kibon.tagesschulen.service.filter.ClientAnmeldungFilter;
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
public class AnmeldungServiceTest {

	@TestSubject
	private final AnmeldungService anmeldungService = new AnmeldungService();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.NICE)
	private EntityManager em;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.NICE)
	private CriteriaBuilder cb;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.NICE)
	private CriteriaQuery<Anmeldung> query;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.NICE)
	private Root<Anmeldung> root;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.NICE)
	private TypedQuery<Anmeldung> tq;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private AnmeldungConverter converter;

	@Test
	public void testOnBetreuungAnfrageCreated() {
		TagesschuleAnmeldungEventDTO dto = new TagesschuleAnmeldungEventDTO();
		dto.setAnmeldungsDetails(new TagesschuleAnmeldungDetailsDTO());
		dto.getAnmeldungsDetails().setRefnr("10");
		LocalDateTime eventTime = LocalDateTime.now();

		Anmeldung anmeldung = new Anmeldung();
		anmeldung.setRefnr("10");
		expect(converter.create(dto, eventTime)).andReturn(anmeldung);

		expect(em.getCriteriaBuilder()).andReturn(cb);
		expect(cb.createQuery(Anmeldung.class)).andReturn(query);
		expect(query.from(Anmeldung.class)).andReturn(root);
		expect(em.createQuery(query)).andReturn(tq);
		List<Anmeldung> anmeldungList = new ArrayList<>();
		expect(tq.getResultList()).andReturn(anmeldungList);

		em.persist(anmeldung);
		expectLastCall();

		replay(em, cb, query, root, converter, tq);

		anmeldungService.onAnmeldungTagesschule(dto, eventTime);

		verify(em, converter);
	}
}
