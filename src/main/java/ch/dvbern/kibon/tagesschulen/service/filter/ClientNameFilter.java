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

package ch.dvbern.kibon.tagesschulen.service.filter;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.clients.model.ClientId_;
import ch.dvbern.kibon.clients.model.Client_;
import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung_;



/**
 * Utility class for filtering criteria queries to only deliver {@link ClientAnmeldung}en with a specific client name.
 */
public class ClientNameFilter implements Restriction<ClientAnmeldung, ClientAnmeldung> {

	@Nonnull
	private final String clientName;

	@Nullable
	private ParameterExpression<String> clientParam;

	public ClientNameFilter(@Nonnull String clientName) {
		this.clientName = clientName;
	}

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<ClientAnmeldung> root, @Nonnull CriteriaBuilder cb) {
		clientParam = cb.parameter(String.class, "clientName");
		Path<String> namePath = root.get(ClientAnmeldung_.client).get(Client_.id).get(ClientId_.clientName);

		return Optional.of(cb.equal(namePath, clientParam));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<ClientAnmeldung> query) {
		query.setParameter(clientParam, clientName);
	}
}
