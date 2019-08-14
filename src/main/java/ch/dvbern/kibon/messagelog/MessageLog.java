package ch.dvbern.kibon.messagelog;

import java.time.Instant;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.messagelog.model.ConsumedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MessageLog {

	private static final Logger LOG = LoggerFactory.getLogger(MessageLog.class);

	@Inject
	EntityManager em;

	@Transactional(TxType.MANDATORY)
	public void processed(@Nonnull UUID eventId) {
		em.persist(new ConsumedMessage(eventId, Instant.now()));
	}

	@Transactional(TxType.MANDATORY)
	public boolean alreadyProcessed(@Nonnull UUID eventId) {
		LOG.debug("Looking for event with id {} in message log", eventId);

		return em.find(ConsumedMessage.class, eventId) != null;
	}
}
