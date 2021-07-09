package ch.dvbern.kibon.shared.model;

import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Gesuchsperiode {

	@Id
	@Nonnull
	private @NotEmpty String id = "";

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDate gueltigAb;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDate gueltigBis;

	@Nonnull
	public String getId() {
		return id;
	}

	public void setId(@Nonnull String id) {
		this.id = id;
	}

	@Nonnull
	public LocalDate getGueltigAb() {
		return gueltigAb;
	}

	public void setGueltigAb(@Nonnull LocalDate gueltigAb) {
		this.gueltigAb = gueltigAb;
	}

	@Nonnull
	public LocalDate getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(@Nonnull LocalDate gueltigBis) {
		this.gueltigBis = gueltigBis;
	}
}
