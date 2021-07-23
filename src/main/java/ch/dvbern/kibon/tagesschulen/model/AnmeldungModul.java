package ch.dvbern.kibon.tagesschulen.model;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

import ch.dvbern.kibon.exchange.commons.types.Intervall;

@Entity
public class AnmeldungModul implements Comparable<AnmeldungModul>{
	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@Column(nullable = false)
	private @NotNull Intervall intervall;

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
	public Intervall getIntervall() {
		return intervall;
	}

	public void setIntervall(@Nonnull Intervall intervall) {
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

	@Override
	public int compareTo(AnmeldungModul o) {
		int compare = getWeekday().compareTo(o.weekday);
		if(compare == 0) {
			compare = getIntervall().name().compareTo(o.intervall.name());
		}
		if(compare == 0) {
			compare = getId().compareTo(o.getId());
		}
		if(compare == 0) {
			compare = getModul().getId().compareTo(o.getModul().getId());
		}
		return compare;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || !getClass().equals(o.getClass())) {
			return false;
		}

		AnmeldungModul that = (AnmeldungModul) o;

		return getId().equals(that.getId()) &&
			getIntervall().equals(that.getIntervall()) &&
			getWeekday().equals(that.getWeekday()) &&
			getAnmeldung().equals(that.getAnmeldung()) &&
			getModul().equals(that.getModul());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getWeekday(), getIntervall());
	}
}
