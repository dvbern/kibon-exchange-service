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

import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity_;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldungDTO;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung_;

/**
 * Utility class for filtering criteria queries to only deliver entries with a specific refnr.
 */
public class RefNrFilter implements Restriction<ClientAnmeldung, ClientAnmeldungDTO> {

	@Nullable
	private final String refnr;

	@Nullable
	private ParameterExpression<String> refnrParam;

	public RefNrFilter(@Nullable String refnr) {
		this.refnr = refnr;
	}

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<ClientAnmeldung> root, @Nonnull CriteriaBuilder cb) {
		if (refnr == null) {
			return Optional.empty();
		}

		Path<String> refnrPath = root.get(ClientAnmeldung_.anmeldung).get(AbstractInstitutionPeriodeEntity_.refnr);
		refnrParam = cb.parameter(String.class, AbstractInstitutionPeriodeEntity_.REFNR);

		return Optional.of(cb.equal(refnrPath, refnrParam));
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<ClientAnmeldungDTO> query) {
		if (refnr == null) {
			return;
		}

		query.setParameter(refnrParam, refnr);
	}
}
