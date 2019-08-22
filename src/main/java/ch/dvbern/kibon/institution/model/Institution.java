package ch.dvbern.kibon.institution.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Institution {

	@Id
	@Nonnull
	private @NotEmpty String id = "";

	@Nonnull
	private @NotEmpty String name = "";

	@Nullable
	private String traegerschaft = null;

	@Embedded
	@Nonnull
	private @NotNull @Valid Adresse adresse = new Adresse();

	@Nonnull
	public String getId() {
		return id;
	}

	public void setId(@Nonnull String id) {
		this.id = id;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nullable
	public String getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(@Nullable String traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	@Nonnull
	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(@Nonnull Adresse adresse) {
		this.adresse = adresse;
	}
}
