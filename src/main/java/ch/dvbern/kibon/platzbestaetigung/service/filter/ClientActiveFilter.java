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

package ch.dvbern.kibon.platzbestaetigung.service.filter;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.platzbestaetigung.model.ClientBetreuungAnfrage;
import ch.dvbern.kibon.platzbestaetigung.model.ClientBetreuungAnfrageDTO;
import ch.dvbern.kibon.platzbestaetigung.model.ClientBetreuungAnfrage_;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;

/**
 * Utility class for filtering criteria queries to only deliver {@link ClientVerfuegung}en that are active.
 */
public class ClientActiveFilter implements Restriction<ClientBetreuungAnfrage, ClientBetreuungAnfrageDTO> {

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<ClientBetreuungAnfrage> root, @Nonnull CriteriaBuilder cb) {
		return Optional.of(cb.isTrue(root.get(ClientBetreuungAnfrage_.active)));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<ClientBetreuungAnfrageDTO> query) {
		// nop
	}
}
