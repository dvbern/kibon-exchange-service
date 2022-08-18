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

package ch.dvbern.kibon.kafka;

import java.util.Arrays;

import javax.annotation.Nonnull;

/**
 * All known event types.
 */
public enum EventType {
	CLIENT_ADDED("ClientAdded"),
	CLIENT_REMOVED("ClientRemoved"),
	CLIENT_MODIFIED("ClientModified"),
	INSTITUTION_CHANGED("InstitutionChanged"),
	VERFUEGUNG_VERFUEGT("VerfuegungVerfuegt"),
	BETREUUNG_ANFRAGE_ADDED("BetreuungAnfrageAdded"),
	ANMELDUNG_TAGESSCHULE("AnmeldungTagesschule"),
	GEMEINDE_CHANGED("GemeindeChanged");

	@Nonnull
	private final String name;

	EventType(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	public static EventType of(@Nonnull String name) {
		return Arrays.stream(values())
			.filter(value -> value.getName().equals(name))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("No EventType found for name " + name));
	}

	@Nonnull
	public String getName() {
		return name;
	}
}
