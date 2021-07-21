package ch.dvbern.kibon.tagesschulen.model;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class AnmeldungModul {
	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@Column(nullable = false)
	private @NotNull String intervall;

	@Nonnull
	@Column(nullable = false)
	private @NotNull Integer weekday;

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "anmeldung_modul_anmeldung_id"), nullable = false, updatable = false)
	private @NotNull Anmeldung anmeldung = new Anmeldung();

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "anmeldung_modul_modul_id"), nullable = false, updatable = false)
	private @NotNull Modul modul = new Modul();

	@Nonnull
	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Nonnull
	public String getIntervall() {
		return intervall;
	}

	public void setIntervall(@Nonnull String intervall) {
		this.intervall = intervall;
	}

	@Nonnull
	public Integer getWeekday() {
		return weekday;
	}

	public void setWeekday(@Nonnull Integer weekday) {
		this.weekday = weekday;
	}

	@Nonnull
	public Anmeldung getAnmeldung() {
		return anmeldung;
	}

	public void setAnmeldung(@Nonnull Anmeldung anmeldung) {
		this.anmeldung = anmeldung;
	}

	@Nonnull
	public Modul getModul() {
		return modul;
	}

	public void setModul(@Nonnull Modul modul) {
		this.modul = modul;
	}
}
