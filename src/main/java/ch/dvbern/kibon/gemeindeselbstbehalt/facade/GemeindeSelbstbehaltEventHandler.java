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
import ch.dvbern.kibon.kafka.BaseEventHandler;
import ch.dvbern.kibon.kafka.EventType;
import ch.dvbern.kibon.verfuegung.service.VerfuegungService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

import static ch.dvbern.kibon.kafka.EventType.GEMEINDE_SELBSTBEHALT_CHANGED;

@ApplicationScoped
public class GemeindeSelbstbehaltEventHandler extends BaseEventHandler<GemeindeSelbstbehaltEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(GemeindeSelbstbehaltEventHandler.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	VerfuegungService verfuegungService;

	@Override
	protected void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull EventType eventType,
		@Nonnull GemeindeSelbstbehaltEventDTO dto) {

		if (GEMEINDE_SELBSTBEHALT_CHANGED == eventType) {
			verfuegungService.onGemeindeSelbstbehaltChanged(dto);
		} else {
			LOG.warn("Unimplemented event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
