package ch.dvbern.kibon.tagesschulen.model;

import java.time.DayOfWeek;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.exchange.commons.types.Intervall;

public class AnmeldungModulDTO {

	@Nonnull
	private String modulId;

	@Nonnull
	private DayOfWeek wochentag;

	@Nonnull
	private Intervall intervall;

	public AnmeldungModulDTO(
		@Nonnull String modulId,
		@Nonnull DayOfWeek wochentag,
		@Nonnull Intervall intervall) {
		this.modulId = modulId;
		this.wochentag = wochentag;
		this.intervall = intervall;
	}

	@Nonnull
	public String getModulId() {
		return modulId;
	}

	public void setModulId(@Nonnull String modulId) {
		this.modulId = modulId;
	}

	@Nonnull
	public DayOfWeek getWochentag() {
		return wochentag;
	}

	public void setWochentag(@Nonnull DayOfWeek wochentag) {
		this.wochentag = wochentag;
	}

	@Nonnull
	public Intervall getIntervall() {
		return intervall;
	}

	public void setIntervall(@Nonnull Intervall intervall) {
		this.intervall = intervall;
	}
}
