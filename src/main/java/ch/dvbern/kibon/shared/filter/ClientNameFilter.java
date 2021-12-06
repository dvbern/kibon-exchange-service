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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.ClientId_;
import ch.dvbern.kibon.clients.model.Client_;
import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;

/**
 * Utility class for filtering criteria queries to only deliver {@link ClientAnmeldung}en with a specific client name.
 */
public class ClientNameFilter<X, Y> implements Restriction<X, Y> {

	@Nonnull
	private final String clientName;

	@Nonnull
	private final SingularAttribute<X, Client> z;

	@Nullable
	private ParameterExpression<String> clientParam;

	public ClientNameFilter(@Nonnull String clientName, @Nonnull SingularAttribute<X, Client> z) {
		this.clientName = clientName;
		this.z = z;
	}

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<X> root, @Nonnull CriteriaBuilder cb) {
		clientParam = cb.parameter(String.class, "clientName");
		Path<String> namePath = root.get(z).get(Client_.id).get(ClientId_.clientName);

		return Optional.of(cb.equal(namePath, clientParam));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<Y> query) {
		query.setParameter(clientParam, clientName);
	}
}
