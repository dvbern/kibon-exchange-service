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

package ch.dvbern.kibon.testutils;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;
import com.spotify.hamcrest.jackson.JsonMatchers;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.comparesEqualTo;

public final class MatcherUtil {

	private MatcherUtil() {
	}

	@Nonnull
	public static Matcher<JsonNode> jsonBigDecimalLike(@Nonnull BigDecimal expected) {
		// Jackson uses scientific representation of BigDecimal, such that comparesEqual must be used for the match
		return JsonMatchers.jsonBigDecimal(comparesEqualTo(expected));
	}
}
