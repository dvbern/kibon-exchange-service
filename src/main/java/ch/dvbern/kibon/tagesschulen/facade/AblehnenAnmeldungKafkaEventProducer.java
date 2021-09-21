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

package ch.dvbern.kibon.tagesschulen.facade;

import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.kafka.EmitterUtil;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AblehnenAnmeldungKafkaEventProducer {
	private static final Logger LOG = LoggerFactory.getLogger(AblehnenAnmeldungKafkaEventProducer.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	@Channel("AnmeldungAblehnenEvents")
	Emitter<String> anmeldungAblehnenEvent;

	@Nonnull
	public CompletionStage<Void> process(
		@Nonnull String refNummer,
		@Nonnull Client client) {

		String eventType = "AnmeldungAblehnenAnfrage";

		return EmitterUtil.emitHelper(eventType, refNummer, client, refNummer)
			.apply(anmeldungAblehnenEvent, LOG);
	}

}
