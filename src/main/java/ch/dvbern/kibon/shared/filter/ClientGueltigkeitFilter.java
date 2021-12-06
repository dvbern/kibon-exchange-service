/*
 * Copyright (C) 2020 DV Bern AG, Switzerland
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

import java.time.LocalDate;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.Client_;
import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity_;

public class ClientGueltigkeitFilter<X, Y> implements Restriction<X, Y> {

	@Nonnull
	private final SingularAttribute<X, ? extends AbstractInstitutionPeriodeEntity> z;

	@Nonnull
	private final SingularAttribute<X, Client> c;

	public ClientGueltigkeitFilter(
		@Nonnull SingularAttribute<X, ? extends AbstractInstitutionPeriodeEntity> z,
		@Nonnull SingularAttribute<X, Client> c) {
		this.z = z;
		this.c = c;
	}

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<X> root, @Nonnull CriteriaBuilder cb) {
		Path<LocalDate> entityVon = root.get(z)
			.get(AbstractInstitutionPeriodeEntity_.periodeVon);
		Path<LocalDate> entityBis = root.get(z)
			.get(AbstractInstitutionPeriodeEntity_.periodeBis);

		Path<Client> clientPath = root.get(c);
		Path<LocalDate> gueltigAb = clientPath.get(Client_.gueltigAb);
		Path<LocalDate> gueltigBis = clientPath.get(Client_.gueltigBis);

		Predicate gueltigAbAfterVon = cb.or(cb.isNull(gueltigBis), cb.lessThanOrEqualTo(entityVon, gueltigBis));
		Predicate gueltigBisBeforeBis = cb.or(cb.isNull(gueltigAb), cb.greaterThanOrEqualTo(entityBis, gueltigAb));
		Predicate gueltigPredicate = cb.and(gueltigAbAfterVon, gueltigBisBeforeBis);

		return Optional.of(gueltigPredicate);
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<Y> query) {

	}
}
