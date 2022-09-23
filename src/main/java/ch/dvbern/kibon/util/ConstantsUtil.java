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

package ch.dvbern.kibon.util;

import java.util.Arrays;
import java.util.List;

public final class ConstantsUtil {

	public static final int TEXT_AREA_SIZE = 4000;
	public static final int SHORT_COLUMN_SIZE = 100;
	public static final String ID_UNKNOWN_INSTITUTION_STAMMDATEN_KITA_BE = "00000000-0000-0000-0000-000000000000";
	public static final String ID_UNKNOWN_INSTITUTION_STAMMDATEN_TAGESFAMILIE_BE =
		"00000000-0000-0000-0000-000000000001";
	public static final String ID_UNKNOWN_INSTITUTION_STAMMDATEN_TAGESSCHULE_BE =
		"00000000-0000-0000-0000-000000000002";

	public static final int MAX_LIMIT = 10000;
	public static final String DEFAULT_LIMIT = "10000";

	private ConstantsUtil() {
	}

	public static final List<String> ALL_UNKNOWN_BE_INSTITUTION_IDS = Arrays.asList(
		ConstantsUtil.ID_UNKNOWN_INSTITUTION_STAMMDATEN_KITA_BE,
		ConstantsUtil.ID_UNKNOWN_INSTITUTION_STAMMDATEN_TAGESFAMILIE_BE,
		ConstantsUtil.ID_UNKNOWN_INSTITUTION_STAMMDATEN_TAGESSCHULE_BE
	);
}
