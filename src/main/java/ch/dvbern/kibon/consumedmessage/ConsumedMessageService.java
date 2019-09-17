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

package ch.dvbern.kibon.consumedmessage;

import java.time.Instant;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ch.dvbern.kibon.consumedmessage.model.ConsumedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ConsumedMessageService {

	private static final Logger LOG = LoggerFactory.getLogger(ConsumedMessageService.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
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
