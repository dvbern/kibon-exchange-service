/*
 * Copyright (C) 2021 DV Bern AG, Switzerland
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DateRange implements Comparable<DateRange> {

	@Nonnull
	private LocalDate gueltigAb = LocalDate.MIN;

	@Nonnull
	private LocalDate gueltigBis = LocalDate.MAX;

	public DateRange() {
	}

	public DateRange(@Nonnull LocalDate gueltigAb, @Nonnull LocalDate gueltigBis) {
		this.gueltigAb = gueltigAb;
		this.gueltigBis = gueltigBis;
	}

	@SuppressWarnings("IncompleteCopyConstructor")
	public DateRange(@Nonnull DateRange gueltigkeit) {
		// no need to copy LocalDates, since they are inherently immutable
		this(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis());
	}

	@Nonnull
	public static DateRange of(@Nullable LocalDate gueltigAb, @Nullable LocalDate gueltigBis) {
		LocalDate ab = Optional.ofNullable(gueltigAb)
			.orElseGet(() -> LocalDate.of(2010, 8, 1));

		LocalDate bis = Optional.ofNullable(gueltigBis)
			.orElseGet(() -> LocalDate.of(3000, 7, 31));

		return new DateRange(ab, bis);
	}

	@Nonnull
	public static SortedSet<DateRange> findZeitraeume(@Nonnull SortedSet<LocalDate> stichtage) {
		SortedSet<DateRange> zeitraeume = new TreeSet<>();
		LocalDate von = stichtage.first();
		stichtage.remove(stichtage.first());

		for (LocalDate stichtag : stichtage) {
			// der Stichtag ist der Tag an dem die Aenderung aktiv wird => der Zeitraum endet einen Tag davor;
			DateRange zeitraum = new DateRange(von, stichtag.minusDays(1));
			zeitraeume.add(zeitraum);

			von = stichtag;
		}

		return zeitraeume;
	}

	/**
	 * @return Falls es zwischen dieser DateRange und otherRange eine zeitliche ueberlappung gibt, so wird diese
	 * zurueck gegeben
	 */
	@Nonnull
	public Optional<DateRange> getOverlap(@Nonnull DateRange otherRange) {
		if (this.getGueltigAb().isAfter(otherRange.getGueltigBis()) ||
			this.getGueltigBis().isBefore(otherRange.getGueltigAb())) {
			return Optional.empty();
		}

		LocalDate ab = otherRange.getGueltigAb().isAfter(this.getGueltigAb()) ?
			otherRange.getGueltigAb() :
			this.getGueltigAb();

		LocalDate bis = otherRange.getGueltigBis().isBefore(this.getGueltigBis()) ?
			otherRange.getGueltigBis() :
			this.getGueltigBis();

		return Optional.of(new DateRange(ab, bis));
	}

	/**
	 * {@link #getOverlap(DateRange)}.isPresent()
	 */
	public boolean intersects(@Nonnull DateRange other) {
		return getOverlap(other).isPresent();
	}

	/**
	 * @return Zeiträume dieser DateRange, welche den Range von {@code other} nicht beinhalten. Falls es keine
	 * Überlappung gibt, wird eine leere Liste zurück gegeben.
	 *
	 * <h1>Beispiel</h1>
	 * <table summary="Beispiel">
	 * <tr>
	 * <th>Objects</th>
	 * <th>Zeitstrahlen</th>
	 * </tr>
	 * <tr>
	 * <td>this</td>
	 * <td><pre>|--------|</pre></td>
	 * </tr>
	 * <tr>
	 * <td>other</td>
	 * <td><pre>   |---|  </pre></td>
	 * </tr>
	 * <tr>
	 * <td>results</td>
	 * <td><pre>|--|   |-|</pre></td>
	 * </tr>
	 * </table>
	 */
	@Nonnull
	public List<DateRange> except(@Nonnull DateRange other) {
		if (!this.intersects(other)) {
			return Collections.emptyList();
		}

		SortedSet<LocalDate> stichtage = Stream.concat(this.streamStichtage(), other.streamStichtage())
			.collect(Collectors.toCollection(TreeSet::new));

		return findZeitraeume(stichtage).stream()
			.filter(r -> !r.intersects(other))
			.collect(Collectors.toList());
	}

	/**
	 * Ein Stichtag ist der Tag, ab dem eine Aenderung aktiv wird.
	 * Der erste Stichtag einer DateRange ist also gueltigAb und der Zweite Stichtag ist der Tag <b>nach</b> gueltigBis
	 */
	@Nonnull
	public Stream<LocalDate> streamStichtage() {
		return Stream.of(getGueltigAb(), getGueltigBis().plusDays(1));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || !getClass().equals(o.getClass())) {
			return false;
		}

		DateRange dateRange = (DateRange) o;
		return getGueltigAb().equals(dateRange.getGueltigAb()) && getGueltigBis().equals(dateRange.getGueltigBis());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getGueltigAb(), getGueltigBis());
	}

	@Override
	public int compareTo(@Nonnull DateRange o) {
		return Comparator.comparing(DateRange::getGueltigAb)
			.thenComparing(DateRange::getGueltigBis)
			.compare(this, o);
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", DateRange.class.getSimpleName() + '[', "]")
			.add("gueltigAb=" + gueltigAb)
			.add("gueltigBis=" + gueltigBis)
			.toString();
	}

	@Nonnull
	public LocalDate getGueltigAb() {
		return gueltigAb;
	}

	@Nonnull
	public LocalDate getGueltigBis() {
		return gueltigBis;
	}
}
