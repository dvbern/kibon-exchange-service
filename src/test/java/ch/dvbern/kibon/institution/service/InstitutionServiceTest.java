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

package ch.dvbern.kibon.institution.service;

import java.util.Collections;

import javax.persistence.EntityManager;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.model.Institution;
import org.easymock.EasyMockExtension;
import org.easymock.LogicalOperator;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static ch.dvbern.kibon.institution.service.InstitutionTestUtil.INSTITUTION_COMPARATOR;
import static ch.dvbern.kibon.institution.service.InstitutionTestUtil.createInstitutionEvent;
import static ch.dvbern.kibon.institution.service.InstitutionTestUtil.fromDTO;
import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@ExtendWith(EasyMockExtension.class)
class InstitutionServiceTest {

	@TestSubject
	private final InstitutionService service = new InstitutionService();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private EntityManager em;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private InstitutionConverter converter;

	@Test
	public void testOnInstitutionChanged_persistNew() {
		InstitutionEventDTO dto = createInstitutionEvent();

		expect(em.find(Institution.class, dto.getId())).andReturn(null);

		Institution institution = fromDTO(dto);
		expect(converter.create(dto)).andReturn(institution);

		em.persist(cmp(institution, INSTITUTION_COMPARATOR, LogicalOperator.EQUAL));
		expectLastCall();

		replay(em, converter);

		service.onInstitutionChanged(dto);

		verify(em, converter);
	}

	@Test
	public void testOnInstitutionChanged_mergeExisting() {
		InstitutionEventDTO dto = createInstitutionEvent();

		Institution existingInstitution = fromDTO(dto);
		expect(em.find(Institution.class, dto.getId())).andReturn(existingInstitution);

		converter.update(existingInstitution, dto);
		expectLastCall();

		expect(em.merge(eq(existingInstitution))).andReturn(existingInstitution);

		replay(em, converter);

		service.onInstitutionChanged(dto);

		verify(em, converter);
	}

	@Test
	public void testGet_shouldReturnEmptyForEmptyInput() {
		replay(em, converter);

		assertThat(service.get(Collections.emptySet()), is(empty()));

		verify(em, converter);
	}
}
