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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.shared.model.AbstractClientEntity;
import ch.dvbern.kibon.shared.model.AbstractClientEntity_;

/**
 * Utility class for filtering criteria queries to only deliver {@link AbstractClientEntity}s that are active.
 */
public class ClientActiveFilter<X extends AbstractClientEntity, Y> implements Restriction<X, Y> {

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<X> root, @Nonnull CriteriaBuilder cb) {
		return Optional.of(cb.isTrue(root.get(AbstractClientEntity_.active)));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<Y> query) {
		// nop
	}
}
