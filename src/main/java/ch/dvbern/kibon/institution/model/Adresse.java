package ch.dvbern.kibon.institution.model;

import java.util.Objects;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Embeddable
public class Adresse {

	@Nonnull
	private @NotNull String strasse = "";

	@Nullable
	private String hausnummer = null;

	@Nullable
	private String adresszusatz = null;

	@Nonnull
	private @NotNull String ort = "";

	@Nonnull
	private @NotNull String plz = "";

	@Nonnull
	private @NotEmpty String land = "";

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Adresse)) {
			return false;
		}

		Adresse adresse = (Adresse) o;

		return getStrasse().equals(adresse.getStrasse()) &&
			Objects.equals(getHausnummer(), adresse.getHausnummer()) &&
			Objects.equals(getAdresszusatz(), adresse.getAdresszusatz()) &&
			getOrt().equals(adresse.getOrt()) &&
			getPlz().equals(adresse.getPlz()) &&
			getLand().equals(adresse.getLand());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getStrasse(), getHausnummer(), getAdresszusatz(), getOrt(), getPlz(), getLand());
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", Adresse.class.getSimpleName() + '[', "]")
			.add("strasse='" + strasse + '\'')
			.add("hausnummer='" + hausnummer + '\'')
			.add("adresszusatz='" + adresszusatz + '\'')
			.add("ort='" + ort + '\'')
			.add("plz='" + plz + '\'')
			.add("land='" + land + '\'')
			.toString();
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
	public String getOrt() {
		return ort;
	}

	public void setOrt(@Nonnull String ort) {
		this.ort = ort;
	}

	@Nonnull
	public String getPlz() {
		return plz;
	}

	public void setPlz(@Nonnull String plz) {
		this.plz = plz;
	}

	@Nonnull
	public String getLand() {
		return land;
	}

	public void setLand(@Nonnull String land) {
		this.land = land;
	}
}
