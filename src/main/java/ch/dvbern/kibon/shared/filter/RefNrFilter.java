/*
 * Copyright (C) 2022 DV Bern AG, Switzerland
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.shared.model.AbstractClientEntity;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity_;

/**
 * Utility class for filtering criteria queries to only deliver entries with a specific refnr.
 */
public class RefNrFilter<X extends AbstractClientEntity, Y> implements Restriction<X, Y> {

	@Nullable
	private final String refnr;

	@Nonnull
	private final SingularAttribute<X, ? extends AbstractInstitutionPeriodeEntity> z;

	@Nullable
	private ParameterExpression<String> refnrParam;

	public RefNrFilter(
		@Nonnull SingularAttribute<X, ? extends AbstractInstitutionPeriodeEntity> z,
		@Nullable String refnr) {
		this.z = z;
		this.refnr = refnr;
	}

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<X> root, @Nonnull CriteriaBuilder cb) {
		if (refnr == null) {
			return Optional.empty();
		}

		Path<String> refnrPath = root.get(z).get(AbstractInstitutionPeriodeEntity_.refnr);
		refnrParam = cb.parameter(String.class, AbstractInstitutionPeriodeEntity_.REFNR);

		return Optional.of(cb.equal(refnrPath, refnrParam));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<Y> query) {
		if (refnr == null) {
			return;
		}

		query.setParameter(refnrParam, refnr);
	}
}
