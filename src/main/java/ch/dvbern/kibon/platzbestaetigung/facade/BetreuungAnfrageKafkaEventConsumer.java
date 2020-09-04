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

package ch.dvbern.kibon.platzbestaetigung.facade;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungAnfrageEventDTO;
import ch.dvbern.kibon.kafka.MessageProcessor;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment.Strategy;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@SuppressWarnings("unused")
@ApplicationScoped
public class BetreuungAnfrageKafkaEventConsumer {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	BetreuungAnfrageEventHandler eventHandler;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	MessageProcessor processor;

	@Incoming("BetreuungAnfrageEvents")
	@Acknowledgment(Strategy.MANUAL)
	public CompletionStage<Void> onMessage(@Nonnull KafkaRecord<String, BetreuungAnfrageEventDTO> message) {

		return CompletableFuture.runAsync(() -> processor.process(message, eventHandler))
			.thenCompose(f -> message.ack());
	}
}