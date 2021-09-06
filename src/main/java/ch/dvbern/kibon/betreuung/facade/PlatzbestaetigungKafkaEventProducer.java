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

package ch.dvbern.kibon.betreuung.facade;

import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungEventDTO;
import ch.dvbern.kibon.kafka.EmitterUtil;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PlatzbestaetigungKafkaEventProducer {

	private static final Logger LOG = LoggerFactory.getLogger(PlatzbestaetigungKafkaEventProducer.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	@Channel("PlatzbestaetigungBetreuungEvents")
	Emitter<BetreuungEventDTO> platzbestaetigungBetreuungEvents;

	/**
	 * Sends a betreuungEventDTO to Kafka.
	 *
	 * @param betreuungEventDTO the payload
	 * @return the {@code CompletionStage}, which will be completed when the message for this payload is acknowledged
	 * by Kafka, or which will completeExceptionally upon NACK, channel cancellation/termination or queue overlflow.
	 */
	@Nonnull
	public CompletionStage<Void> process(@Nonnull BetreuungEventDTO betreuungEventDTO, @Nonnull Client client) {
		String eventType = "PlatzbestaetigungBetreuung";
		String key = betreuungEventDTO.getRefnr();

		return EmitterUtil.emitHelper(eventType, key, client, betreuungEventDTO)
			.apply(platzbestaetigungBetreuungEvents, LOG);
	}
}
