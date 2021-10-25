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

package ch.dvbern.kibon.shared.filter;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import ch.dvbern.kibon.persistence.Restriction;

/**
 * Utility class for filtering criteria queries to only deliver entries with an ID > the specified one.
 */
public class AfterIdFilter<X, Y> implements Restriction<X, Y> {

	@Nullable
	private final Long afterId;

	@Nonnull
	private final SingularAttribute<? super X, Long> z;

	@Nullable
	private ParameterExpression<Long> param;

	public AfterIdFilter(@Nullable Long afterId, @Nonnull SingularAttribute<? super X, Long> z) {
		this.afterId = afterId;
		this.z = z;
	}

	@Override
	@Nonnull
	public Optional<Predicate> getPredicate(@Nonnull Root<X> root, @Nonnull CriteriaBuilder cb) {
		if (afterId == null) {
			return Optional.empty();
		}

		param = cb.parameter(Long.class, "id");


		return Optional.of(cb.greaterThan(root.get(z), param));
	}


	@Override
	public void setParameter(@Nonnull TypedQuery<Y> query) {
		if (afterId == null) {
			return;
		}

		query.setParameter(param, afterId);
	}
}
