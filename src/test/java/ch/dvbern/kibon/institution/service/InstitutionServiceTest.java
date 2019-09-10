package ch.dvbern.kibon.institution.service;

import java.util.Collections;

import javax.persistence.EntityManager;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.testutils.EasyMockExtension;
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
	public void testInstitutionChanged_persistNew() {
		InstitutionEventDTO dto = createInstitutionEvent();

		expect(em.find(Institution.class, dto.getId())).andReturn(null);

		Institution institution = fromDTO(dto);
		expect(converter.create(dto)).andReturn(institution);

		em.persist(cmp(institution, INSTITUTION_COMPARATOR, LogicalOperator.EQUAL));
		expectLastCall();

		replay(em, converter);

		service.institutionChanged(dto);

		verify(em, converter);
	}

	@Test
	public void testInstitutionChanged_mergeExisting() {
		InstitutionEventDTO dto = createInstitutionEvent();

		Institution existingInstitution = fromDTO(dto);
		expect(em.find(Institution.class, dto.getId())).andReturn(existingInstitution);

		converter.update(existingInstitution, dto);
		expectLastCall();

		expect(em.merge(eq(existingInstitution))).andReturn(existingInstitution);

		replay(em, converter);

		service.institutionChanged(dto);

		verify(em, converter);
	}

	@Test
	public void testGet_shouldReturnEmptyForEmptyInput() {
		replay(em, converter);

		assertThat(service.get(Collections.emptySet()), is(empty()));

		verify(em, converter);
	}
}
