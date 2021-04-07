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

import java.time.LocalDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.platzbestaetigung.BetreuungAnfrageEventDTO;
import ch.dvbern.kibon.kafka.BaseEventHandler;
import ch.dvbern.kibon.kafka.EventType;
import ch.dvbern.kibon.betreuung.service.BetreuungAnfrageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.kibon.kafka.EventType.BETREUUNG_ANFRAGE_ADDED;

@ApplicationScoped
public class BetreuungAnfrageEventHandler extends BaseEventHandler<BetreuungAnfrageEventDTO> {

	private static final Logger LOG = LoggerFactory.getLogger(BetreuungAnfrageEventHandler.class);

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	BetreuungAnfrageService betreuungAnfrageService;

	@Override
	protected void processEvent(
		@Nonnull UUID eventId,
		@Nonnull LocalDateTime eventTime,
		@Nonnull EventType eventType,
		@Nonnull BetreuungAnfrageEventDTO dto) {

		if (BETREUUNG_ANFRAGE_ADDED == eventType) {
			betreuungAnfrageService.onBetreuungAnfrageCreated(dto, eventTime);
		} else {
			LOG.warn("Unimplemented event type '{}' with id '{}'", eventType, eventId);
		}
	}
}
