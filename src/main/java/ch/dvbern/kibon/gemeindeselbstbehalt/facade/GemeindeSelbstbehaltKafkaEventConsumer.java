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

package ch.dvbern.kibon.gemeindeselbstbehalt.facade;

import ch.dvbern.kibon.exchange.commons.verfuegungselbstbehaltgemeinde.GemeindeSelbstbehaltEventDTO;
import ch.dvbern.kibon.kafka.IncomingEvent;
import ch.dvbern.kibon.kafka.MessageProcessor;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@SuppressWarnings("unused")
@ApplicationScoped
public class GemeindeSelbstbehaltKafkaEventConsumer {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
    GemeindeSelbstbehaltEventHandler eventHandler;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	MessageProcessor processor;

	@Incoming("GemeindeSelbstbehaltEvents")
	@Outgoing("GemeindeSelbstbehaltEvents-internal")
	@Nullable
	public Message<IncomingEvent<GemeindeSelbstbehaltEventDTO>> wrap(
		@Nonnull IncomingKafkaRecord<String, GemeindeSelbstbehaltEventDTO> msg) {

		return processor.toIncomingEvent(msg);
	}

	@Incoming("GemeindeSelbstbehaltEvents-internal")
	@Blocking
	public void onMessage(@Nonnull IncomingEvent<GemeindeSelbstbehaltEventDTO> event) {
		processor.process(event, eventHandler);
	}
}
