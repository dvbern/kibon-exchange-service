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

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.exchange.commons.verfuegung.VerfuegungEventDTO;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity_;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;
import ch.dvbern.kibon.verfuegung.model.Verfuegung_;
import org.easymock.EasyMockExtension;
import org.easymock.EasyMockSupport;
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
class VerfuegungServiceTest extends EasyMockSupport {

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

		mockFindVerfuegung(dto.getRefnr(), dto.getVersion());

		em.persist(verfuegung);
		expectLastCall();

		replayAll();

		service.onVerfuegungCreated(dto);

		verify(em, converter);
	}

	private void mockFindVerfuegung(@Nonnull String refnr, int version) {
		CriteriaBuilder cb = mock(CriteriaBuilder.class);
		expect(em.getCriteriaBuilder()).andReturn(cb);

		CriteriaQuery<Verfuegung> query = mock(CriteriaQuery.class);
		expect(cb.createQuery(Verfuegung.class)).andReturn(query);

		Root<Verfuegung> root = mock(Root.class);
		expect(query.from(Verfuegung.class)).andReturn(root);

		ParameterExpression<String> refnrParam = mock(ParameterExpression.class);
		expect(cb.parameter(String.class, AbstractInstitutionPeriodeEntity_.REFNR)).andReturn(refnrParam);

		Path<String> refnrPath = mock(Path.class);
		expect(root.get(AbstractInstitutionPeriodeEntity_.refnr)).andReturn(refnrPath);

		Predicate refnrPredicate = mock(Predicate.class);
		expect(cb.equal(refnrPath, refnrParam)).andReturn(refnrPredicate);

		ParameterExpression<Integer> versionParam = mock(ParameterExpression.class);
		expect(cb.parameter(Integer.class, Verfuegung_.VERSION)).andReturn(versionParam);

		Path<Integer> versionPath = mock(Path.class);
		expect(root.get(Verfuegung_.version)).andReturn(versionPath);

		Predicate versionPredicate = mock(Predicate.class);
		expect(cb.equal(versionPath, versionParam)).andReturn(versionPredicate);

		expect(query.where(refnrPredicate, versionPredicate)).andReturn(query);

		TypedQuery<Verfuegung> q = mock(TypedQuery.class);
		expect(em.createQuery(query)).andReturn(q);
		expect(q.setParameter(refnrParam, refnr)).andReturn(q);
		expect(q.setParameter(versionParam, version)).andReturn(q);
		expect(q.getResultList()).andReturn(new ArrayList<>());
	}
}
