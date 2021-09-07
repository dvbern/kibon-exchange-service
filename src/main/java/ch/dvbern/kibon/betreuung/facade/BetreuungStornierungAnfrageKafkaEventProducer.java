/*
 * Copyright (C) 2020 DV Bern AG, Switzerland
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

import ch.dvbern.kibon.betreuung.model.BetreuungStornierungAnfrage;
import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.kafka.EmitterUtil;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BetreuungStornierungAnfrageKafkaEventProducer {

	private static final Logger LOG = LoggerFactory.getLogger(BetreuungStornierungAnfrageKafkaEventProducer.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	@Channel("BetreuungStornierungEvents")
	Emitter<String> betreuungStornierungEvent;

	@Nonnull
	public CompletionStage<Void> process(
		@Nonnull BetreuungStornierungAnfrage betreuungStornierungAnfrage,
		@Nonnull Client client) {

		String eventType = "BetreuungStornierungAnfrage";
		String key = betreuungStornierungAnfrage.getRefnr();

		return EmitterUtil.emitHelper(eventType, key, client, key)
			.apply(betreuungStornierungEvent, LOG);
	}
}
