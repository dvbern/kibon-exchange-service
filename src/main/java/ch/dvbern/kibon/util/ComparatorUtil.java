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

import java.util.Comparator;

import javax.annotation.Nonnull;

import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity;

public final class ComparatorUtil {

	private ComparatorUtil() {
	}

	/**
	 * A generic IdentityComparator, to help Java's type inference.
	 *
	 * @see <a href="https://blog.jooq.org/2014/01/31/java-8-friday-goodies-lambdas-and-sorting/">
	 * https://blog.jooq.org/2014/01/31/java-8-friday-goodies-lambdas-and-sorting/</a>
	 */
	@Nonnull
	public static <E> Comparator<E> compare() {
		return (e1, e2) -> 0;
	}

	public static <T extends AbstractInstitutionPeriodeEntity> Comparator<T> baseComparator() {
		return ComparatorUtil.<T>compare()
			.thenComparing(T::getRefnr)
			.thenComparing(T::getInstitutionId)
			.thenComparing(T::getPeriodeVon)
			.thenComparing(T::getPeriodeBis);
	}
}
