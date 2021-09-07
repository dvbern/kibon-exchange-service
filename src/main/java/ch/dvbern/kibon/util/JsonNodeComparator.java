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

package ch.dvbern.kibon.util;

import java.io.Serializable;
import java.util.Comparator;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeComparator implements Comparator<JsonNode>, Serializable {

	private static final long serialVersionUID = 5487082947413815262L;

	public static final JsonNodeComparator INSTANCE = new JsonNodeComparator();

	@Override
	public int compare(@Nonnull JsonNode o1, @Nonnull JsonNode o2) {
		if (o1.equals(o2)) {
			return 0;
		}

		return 1;
	}
}
