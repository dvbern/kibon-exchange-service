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

package ch.dvbern.kibon.betreuung.service.filter;

import java.time.LocalDate;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrage;
import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrageDTO;
import ch.dvbern.kibon.betreuung.model.ClientBetreuungAnfrage_;
import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.clients.model.Client_;
import ch.dvbern.kibon.persistence.Restriction;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity_;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldungDTO;
import ch.dvbern.kibon.tagesschulen.model.ClientAnmeldung_;

public class ClientGueltigkeitFilter implements Restriction<ClientBetreuungAnfrage, ClientBetreuungAnfrageDTO> {

	@Nonnull
	@Override
	public Optional<Predicate> getPredicate(@Nonnull Root<ClientBetreuungAnfrage> root, @Nonnull CriteriaBuilder cb) {
		Path<LocalDate> entityVon = root.get(ClientBetreuungAnfrage_.betreuungAnfrage)
			.get(AbstractInstitutionPeriodeEntity_.periodeVon);
		Path<LocalDate> entityBis = root.get(ClientBetreuungAnfrage_.betreuungAnfrage)
			.get(AbstractInstitutionPeriodeEntity_.periodeBis);

		Path<Client> clientPath = root.get(ClientBetreuungAnfrage_.client);
		Path<LocalDate> gueltigAb = clientPath.get(Client_.gueltigAb);
		Path<LocalDate> gueltigBis = clientPath.get(Client_.gueltigBis);

		Predicate gueltigAbAfterVon = cb.or(cb.isNull(gueltigBis), cb.lessThanOrEqualTo(entityVon, gueltigBis));
		Predicate gueltigBisBeforeBis = cb.or(cb.isNull(gueltigAb), cb.greaterThanOrEqualTo(entityBis, gueltigAb));
		Predicate gueltigPredicate = cb.and(gueltigAbAfterVon, gueltigBisBeforeBis);

		return Optional.of(gueltigPredicate);
	}

	@Override
	public void setParameter(@Nonnull TypedQuery<ClientBetreuungAnfrageDTO> query) {

	}
}
