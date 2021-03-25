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

package ch.dvbern.kibon.institution.model;

import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Embeddable
public class KontaktAngaben {

	@Nullable
	private String anschrift = null;

	@Nonnull
	private @NotNull String strasse = "";

	@Nullable
	private String hausnummer = null;

	@Nullable
	private String adresszusatz = null;

	@Nonnull
	private @NotNull String plz = "";

	@Nonnull
	private @NotNull String ort = "";

	@Nonnull
	private @NotEmpty String land = "";

	@Nullable
	@Embedded
	@AttributeOverride(name = "name", column = @Column(name = "gemeinde_name"))
	private Gemeinde gemeinde = null;

	@Nullable
	private String email = null;

	@Nullable
	private String alternativeEmail = null;

	@Nullable
	private String telefon = null;

	@Nullable
	private String webseite = null;

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", KontaktAngaben.class.getSimpleName() + '[', "]")
			.add("strasse='" + strasse + '\'')
			.add("hausnummer='" + hausnummer + '\'')
			.add("adresszusatz='" + adresszusatz + '\'')
			.add("plz='" + plz + '\'')
			.add("ort='" + ort + '\'')
			.add("land='" + land + '\'')
			.toString();
	}

	@Nullable
	public String getAnschrift() {
		return anschrift;
	}

	public void setAnschrift(@Nullable String anschrift) {
		this.anschrift = anschrift;
	}

	@Nonnull
	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(@Nonnull String strasse) {
		this.strasse = strasse;
	}

	@Nullable
	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(@Nullable String hausnummer) {
		this.hausnummer = hausnummer;
	}

	@Nullable
	public String getAdresszusatz() {
		return adresszusatz;
	}

	public void setAdresszusatz(@Nullable String adresszusatz) {
		this.adresszusatz = adresszusatz;
	}

	@Nonnull
	public String getPlz() {
		return plz;
	}

	public void setPlz(@Nonnull String plz) {
		this.plz = plz;
	}

	@Nonnull
	public String getOrt() {
		return ort;
	}

	public void setOrt(@Nonnull String ort) {
		this.ort = ort;
	}

	@Nonnull
	public String getLand() {
		return land;
	}

	public void setLand(@Nonnull String land) {
		this.land = land;
	}

	@Nullable
	public Gemeinde getGemeinde() {
		return gemeinde;
	}

	public void setGemeinde(@Nullable Gemeinde gemeinde) {
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

	public void setWebseite(@Nullable String website) {
		this.webseite = website;
	}

	@Nullable
	public String getAlternativeEmail() {
		return alternativeEmail;
	}

	public void setAlternativeEmail(@Nullable String alternativeEmail) {
		this.alternativeEmail = alternativeEmail;
	}
}
