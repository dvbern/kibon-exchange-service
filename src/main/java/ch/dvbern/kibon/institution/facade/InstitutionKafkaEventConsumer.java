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

package ch.dvbern.kibon.institution.facade;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.kafka.MessageProcessor;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@SuppressWarnings("unused")
@ApplicationScoped
public class InstitutionKafkaEventConsumer {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	InstitutionEventHandler eventHandler;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	MessageProcessor processor;

	@Incoming("InstitutionEvents")
	public CompletableFuture<Void> onMessage(@Nonnull KafkaMessage<String, InstitutionEventDTO> message) {

		return CompletableFuture.runAsync(() -> processor.process(message, eventHandler));
	}
}
