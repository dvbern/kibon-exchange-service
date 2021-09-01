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
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungEventDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungStatus;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity_;
import ch.dvbern.kibon.tagesschulen.model.Anmeldung;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static ch.dvbern.kibon.tagesschulen.service.AnmeldungTagesschuleTestUtil.createTagesschuleAnmeldungTestDTO;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@ExtendWith(EasyMockExtension.class)
public class AnmeldungServiceTest extends EasyMockSupport {

	@TestSubject
	private final AnmeldungService anmeldungService = new AnmeldungService();

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Mock(type = MockType.STRICT)
	private EntityManager em;

	@BeforeEach
	void setUp() {
		anmeldungService.converter = new AnmeldungConverter();
		anmeldungService.converter.mapper = new ObjectMapper();
	}

	@SuppressWarnings("JUnitTestMethodWithNoAssertions")
	@Test
	void testOnBetreuungAnfrageCreated_insertsWhenNew() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		LocalDateTime eventTime = LocalDateTime.now();

		mockGetLatestAnmeldung(dto.getAnmeldungsDetails().getRefnr(), Stream.empty());

		em.persist(EasyMock.anyObject(Anmeldung.class));
		expectLastCall();

		replayAll();

		anmeldungService.onAnmeldungTagesschule(dto, eventTime);

		verifyAll();
	}

	@SuppressWarnings("JUnitTestMethodWithNoAssertions")
	@Test
	void testOnBetreuungAnfrageCreated_insertsNewWhenChangedSignificantly() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		LocalDateTime eventTime = LocalDateTime.now();

		Anmeldung existingAnmeldung = createAnmeldungFromDTO(dto, eventTime);

		// change something that Anmeldung COMPARATOR checks
		dto.setVersion(1);

		mockGetLatestAnmeldung(dto.getAnmeldungsDetails().getRefnr(), Stream.of(existingAnmeldung));

		em.persist(EasyMock.not(EasyMock.eq(existingAnmeldung)));
		expectLastCall();

		replayAll();

		anmeldungService.onAnmeldungTagesschule(dto, eventTime);

		verifyAll();
	}

	@Test
	void testOnBetreuungAnfrageCreated_updatesStatusWhenChangedInsignificantly() {
		TagesschuleAnmeldungEventDTO dto = createTagesschuleAnmeldungTestDTO();
		LocalDateTime eventTime = LocalDateTime.now();

		Anmeldung existingAnmeldung = createAnmeldungFromDTO(dto, eventTime);
		TagesschuleAnmeldungStatus testStatus = TagesschuleAnmeldungStatus.SCHULAMT_ANMELDUNG_ABGELEHNT;
		assertThat("verify setup", existingAnmeldung.getStatus(), is(not(testStatus)));

		// change something that Anmeldung COMPARATOR ignores
		dto.setStatus(testStatus);

		mockGetLatestAnmeldung(dto.getAnmeldungsDetails().getRefnr(), Stream.of(existingAnmeldung));

		expect(em.merge(existingAnmeldung)).andReturn(existingAnmeldung);

		replayAll();

		anmeldungService.onAnmeldungTagesschule(dto, eventTime);

		verifyAll();

		assertThat(existingAnmeldung.getStatus(), is(testStatus));
	}

	@Nonnull
	private Anmeldung createAnmeldungFromDTO(TagesschuleAnmeldungEventDTO dto, LocalDateTime eventTime) {
		AnmeldungConverter localConverter = new AnmeldungConverter();
		localConverter.mapper = new ObjectMapper();

		return localConverter.create(dto, eventTime);
	}

	private void mockGetLatestAnmeldung(@Nonnull String refnr, @Nonnull Stream<Anmeldung> result) {
		CriteriaBuilder cb = mock(CriteriaBuilder.class);
		expect(em.getCriteriaBuilder()).andReturn(cb);

		CriteriaQuery<Anmeldung> query = mock(CriteriaQuery.class);
		expect(cb.createQuery(Anmeldung.class)).andReturn(query);

		Root<Anmeldung> root = mock(Root.class);
		expect(query.from(Anmeldung.class)).andReturn(root);

		ParameterExpression<String> refNrParam = mock(ParameterExpression.class);
		expect(cb.parameter(String.class, "refnr")).andReturn(refNrParam);

		Path<String> refNrPath = mock(Path.class);
		expect(root.get(AbstractInstitutionPeriodeEntity_.refnr)).andReturn(refNrPath);

		Predicate refnrPredicate = mock(Predicate.class);
		expect(cb.equal(refNrPath, refNrParam)).andReturn(refnrPredicate);

		expect(query.where(refnrPredicate)).andReturn(query);

		Path<Long> idPath = mock(Path.class);
		expect(root.get(AbstractInstitutionPeriodeEntity_.id)).andReturn(idPath);

		Order order = mock(Order.class);
		expect(cb.desc(idPath)).andReturn(order);
		expect(query.orderBy(order)).andReturn(query);

		TypedQuery<Anmeldung> q = mock(TypedQuery.class);
		expect(em.createQuery(query)).andReturn(q);
		expect(q.setParameter(refNrParam, refnr)).andReturn(q);
		expect(q.setMaxResults(1)).andReturn(q);
		expect(q.getResultStream()).andReturn(result);
	}
}
