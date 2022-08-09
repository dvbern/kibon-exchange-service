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

package ch.dvbern.kibon.shared.filter;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.api.shared.ClientInstitutionFilterParams;
import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrage;
import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrageDTO;
import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrage_;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldungDTO;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung_;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegungDTO;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung_;

@SuppressWarnings("PMD.ClassNamingConventions")
public final class FilterControllerFactory {

	private FilterControllerFactory() {
	}

	@Nonnull
	public static FilterController<ClientAnmeldung, ClientAnmeldungDTO> anmeldungenFilter(
		@Nonnull String clientName,
		@Nonnull ClientInstitutionFilterParams filterParams) {

		return new FilterController<>(ClientAnmeldung_.anmeldung, clientName, filterParams);
	}

	@Nonnull
	public static FilterController<ClientBetreuungAnfrage, ClientBetreuungAnfrageDTO> betreuungAnfrageFilter(
		@Nonnull String clientName,
		@Nonnull ClientInstitutionFilterParams filterParams) {

		return new FilterController<>(ClientBetreuungAnfrage_.betreuungAnfrage, clientName, filterParams);
	}

	@Nonnull
	public static FilterController<ClientVerfuegung, ClientVerfuegungDTO> verfuegungenFilter(
		@Nonnull String clientName,
		@Nonnull ClientInstitutionFilterParams filterParams) {

		return new FilterController<>(ClientVerfuegung_.verfuegung, clientName, filterParams);
	}
}
