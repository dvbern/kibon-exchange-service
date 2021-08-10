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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;

/**
 * Helper class for filtering {@link ClientAnmeldung}en.
 */
public class ClientAnmeldungFilter {

	@Nullable
	private final Integer limit;
	@Nonnull
	private final List<Restriction<ClientAnmeldung, ClientAnmeldung>> restrictions = new ArrayList<>();

	/**
	 * For filtering by clientName only.
	 */
	public ClientAnmeldungFilter(@Nonnull String clientName) {
		this(clientName, null, null);
	}

	/**
	 * @param clientName the clients name
	 * @param afterId for pagination, the id after which results are wanted
	 * @param limit max. amount of results
	 */
	public ClientAnmeldungFilter(
		@Nonnull String clientName,
		@Nullable Long afterId,
		@Nullable Integer limit) {

		restrictions.add(new ClientActiveFilter());
		restrictions.add(new ClientNameFilter(clientName));
		restrictions.add(new AfterIdFilter(afterId));

		this.limit = limit;
	}

	/**
	 * Sets the filter predicates on the given query.
	 */
	public void setPredicate(
		@Nonnull CriteriaQuery<ClientAnmeldung> query,
		@Nonnull Root<ClientAnmeldung> root,
		@Nonnull CriteriaBuilder cb) {

		Predicate[] predicates = restrictions.stream()
			.map(r -> r.getPredicate(root, cb))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toArray(Predicate[]::new);

		query.where(predicates);
	}

	/**
	 * Sets the filter parameters on the given query.
	 */
	public void setParameters(@Nonnull TypedQuery<ClientAnmeldung> query) {
		restrictions.forEach(r -> r.setParameter(query));

		if (limit != null) {
			query.setMaxResults(limit);
		}
	}
}
