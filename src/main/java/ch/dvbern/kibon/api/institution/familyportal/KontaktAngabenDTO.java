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

package ch.dvbern.kibon.api.institution.familyportal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.kibon.exchange.api.common.institution.AdresseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KontaktAngabenDTO extends AdresseDTO {

	private static final long serialVersionUID = -4679630124896232877L;

	@Nullable
	private String anschrift = null;

	@Nonnull
	private GemeindeDTO gemeinde = new GemeindeDTO();

	@Nullable
	private String email = null;

	@Nullable
	private String telefon = null;

	@Nullable
	private String webseite = null;

	@Nullable
	public String getAnschrift() {
		return anschrift;
	}

	public void setAnschrift(@Nullable String anschrift) {
		this.anschrift = anschrift;
	}

	@Nonnull
	public GemeindeDTO getGemeinde() {
		return gemeinde;
	}

	public void setGemeinde(@Nonnull GemeindeDTO gemeinde) {
		this.gemeinde = gemeinde;
	}

	@Nullable
	public String getEmail() {
		return email;
	}

	public void setEmail(@Nullable String email) {
		this.email = email;
	}

	@Nullable
	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(@Nullable String telefon) {
		this.telefon = telefon;
	}

	@Nullable
	public String getWebseite() {
		return webseite;
	}

	public void setWebseite(@Nullable String webseite) {
		this.webseite = webseite;
	}
}
