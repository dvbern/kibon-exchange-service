package ch.dvbern.kibon.institution.service;

import javax.persistence.EntityManager;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.institution.model.Institution;
import org.easymock.LogicalOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.dvbern.kibon.institution.service.InstitutionTestUtil.INSTITUTION_COMPARATOR;
import static ch.dvbern.kibon.institution.service.InstitutionTestUtil.createInstitutionEvent;
import static ch.dvbern.kibon.institution.service.InstitutionTestUtil.fromDTO;
import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.strictMock;
import static org.easymock.EasyMock.verify;

class InstitutionServiceTest {

	private final InstitutionService service = new InstitutionService();

	@BeforeEach
	public void setup() {
		service.em = strictMock(EntityManager.class);
		service.converter = strictMock(InstitutionConverter.class);
	}

	@Test
	public void testInstitutionChanged_persistNew() {
		InstitutionEventDTO dto = createInstitutionEvent();

		expect(service.em.find(Institution.class, dto.getId())).andReturn(null);

		Institution institution = fromDTO(dto);
		expect(service.converter.create(dto)).andReturn(institution);

		service.em.persist(cmp(institution, INSTITUTION_COMPARATOR, LogicalOperator.EQUAL));
		expectLastCall();

		replay(service.em, service.converter);

		service.institutionChanged(dto);

		verify(service.em, service.converter);
	}

	@Test
	public void testInstitutionChanged_mergeExisting() {
		InstitutionEventDTO dto = createInstitutionEvent();

		Institution existingInstitution = fromDTO(dto);
		expect(service.em.find(Institution.class, dto.getId())).andReturn(existingInstitution);

		service.converter.update(existingInstitution, dto);
		expectLastCall();

		expect(service.em.merge(eq(existingInstitution))).andReturn(existingInstitution);

		replay(service.em, service.converter);

		service.institutionChanged(dto);

		verify(service.em, service.converter);
	}
}
