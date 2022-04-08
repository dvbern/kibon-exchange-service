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

package ch.dvbern.kibon.tagesschulen.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.exchange.api.common.tagesschule.ModulDTO;
import ch.dvbern.kibon.exchange.api.common.tagesschule.TagesschuleModuleDTO;
import ch.dvbern.kibon.institution.model.Institution_;
import ch.dvbern.kibon.tagesschulen.model.Modul;
import ch.dvbern.kibon.tagesschulen.model.Modul_;
import ch.dvbern.kibon.tagesschulen.model.TagesschuleModule;
import ch.dvbern.kibon.tagesschulen.model.TagesschuleModule_;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class TagesschuleModuleService {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	EntityManager em;

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper objectMapper;

	@Nonnull
	public Optional<TagesschuleModuleDTO> find(
		@Nonnull Client client,
		@Nonnull LocalDate periodeVon,
		@Nonnull LocalDate periodeBis) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Modul> query = cb.createQuery(Modul.class);
		Root<Modul> root = query.from(Modul.class);

		Join<Modul, TagesschuleModule> periodeJoin = root.join(Modul_.parent);
		Path<String> institutionPath = periodeJoin.get(TagesschuleModule_.institution).get(Institution_.id);

		ParameterExpression<String> idParam = cb.parameter(String.class, Institution_.ID);
		Predicate institutionPredicate = cb.equal(institutionPath, idParam);

		ParameterExpression<LocalDate> periodeVonParam = cb.parameter(LocalDate.class, TagesschuleModule_.PERIODE_VON);
		Predicate periodeVonPredicate = cb.equal(periodeJoin.get(TagesschuleModule_.periodeVon), periodeVonParam);

		ParameterExpression<LocalDate> periodeBisParam = cb.parameter(LocalDate.class, TagesschuleModule_.PERIODE_BIS);
		Predicate periodeBisPredicate = cb.equal(periodeJoin.get(TagesschuleModule_.periodeBis), periodeBisParam);

		query.where(institutionPredicate, periodeVonPredicate, periodeBisPredicate);

		String institutionId = client.getId().getInstitutionId();

		List<Modul> module = em.createQuery(query)
			.setParameter(idParam, institutionId)
			.setParameter(periodeVonParam, periodeVon)
			.setParameter(periodeBisParam, periodeBis)
			.getResultList();

		if (module.isEmpty()) {
			return Optional.empty();
		}

		List<ModulDTO> modulDTOs = module.stream()
			.map(this::convert)
			.collect(Collectors.toList());

		return Optional.of(new TagesschuleModuleDTO(institutionId, periodeVon, periodeBis, modulDTOs));
	}

	@Nonnull
	private ModulDTO convert(@Nonnull Modul model) {
		return objectMapper.convertValue(model, ModulDTO.class);
	}
}
