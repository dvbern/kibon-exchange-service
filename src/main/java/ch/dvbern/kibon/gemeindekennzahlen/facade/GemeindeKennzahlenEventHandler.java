/*
 * Copyright (C) 2022 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.gemeindekennzahlen.facade;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.gemeindekennzahlen.GemeindeKennzahlenEventDTO;
import ch.dvbern.kibon.gemeindekennzahlen.service.GemeindeKennzahlenService;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import ch.dvbern.kibon.kafka.EventType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.kibon.kafka.EventType.GEMEINDE_KENNZAHLEN_CHANGED;
import static ch.dvbern.kibon.kafka.EventType.GEMEINDE_KENNZAHLEN_REMOVED;

@ApplicationScoped
public class GemeindeKennzahlenEventHandler extends BaseEventHandler<GemeindeKennzahlenEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(GemeindeKennzahlenEventHandler.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	GemeindeKennzahlenService gemeindeKennzahlenService;

	@Override
	protected void processEvent(
		@NotNull UUID eventId,
		@NotNull LocalDateTime eventTime,
		@NotNull EventType eventType,
		@NotNull GemeindeKennzahlenEventDTO dto) {
		if (GEMEINDE_KENNZAHLEN_CHANGED == eventType) {
			gemeindeKennzahlenService.onGemeindeKennzahlenChanged(dto);
		} else if (GEMEINDE_KENNZAHLEN_REMOVED == eventType) {
			gemeindeKennzahlenService.onGemeindeKennzahlenRemoved(dto);
		} else {
			LOG.warn("Unimplemented event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
