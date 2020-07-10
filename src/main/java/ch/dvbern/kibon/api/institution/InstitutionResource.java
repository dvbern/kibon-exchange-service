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

package ch.dvbern.kibon.api.institution;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.dvbern.kibon.api.institution.familyportal.AltersKategorie;
import ch.dvbern.kibon.api.institution.familyportal.FamilyPortalDTO;
import ch.dvbern.kibon.api.institution.familyportal.FamilyPortalInstitutionDTO;
import ch.dvbern.kibon.api.institution.familyportal.KontaktAngabenDTO;
import ch.dvbern.kibon.exchange.api.verfuegung.model.BetreuungsAngebot;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.jboss.resteasy.annotations.cache.NoCache;

@Path("/institutions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InstitutionResource {

	@GET
	@Path("/familyportal")
	@Operation(
		summary = "Institutions for family portal Bern",
		description = "Returns a list of institutions with additional data as required for the family portal Bern."
	)
	@SecurityRequirement(name = "OAuth2", scopes = "familyportal")
	@APIResponse(responseCode = "200", name = "FamilyPortalDTO")
	@APIResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
	@APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
	@Transactional
	@NoCache
	@Nonnull
	@RolesAllowed("familyportal")
	@Timed(name = "requestTimer",
		description = "A measure of how long it takes to load FamilyPortalDTO",
		unit = MetricUnits.MILLISECONDS)
	public FamilyPortalDTO getForFamilyPortal() {
		FamilyPortalDTO dto = new FamilyPortalDTO();

		dto.getInstitutionen().add(createStub1());

		return dto;
	}

	@Nonnull
	private FamilyPortalInstitutionDTO createStub1() {
		FamilyPortalInstitutionDTO institution = new FamilyPortalInstitutionDTO();
		institution.setId(UUID.randomUUID().toString());
		institution.setBetreuungsArt(BetreuungsAngebot.KITA);
		institution.setTraegerschaft(null);
		institution.setName("stub1");

		institution.setKontaktAdresse(createKontaktAngabenDV());
		institution.setBetreuungsAdressen(Collections.singletonList(createKontaktAngabenDV()));

		institution.setOeffnungsTage(Arrays.asList(DayOfWeek.FRIDAY, DayOfWeek.MONDAY, DayOfWeek.THURSDAY));
		institution.setOffenVon(LocalTime.of(7, 0));
		institution.setOffenBis(LocalTime.of(19, 0));
		institution.setOeffnungsAbweichungen("Freitags nur bis 17 Uhr");

		institution.setAltersKategorien(Arrays.asList(
			AltersKategorie.KINDERGARTEN,
			AltersKategorie.BABY,
			AltersKategorie.VORSCHULE));

		institution.setAnzahlPlaetze(BigDecimal.valueOf(26.5));
		institution.setAnzahlPlaetzeFirmen(BigDecimal.valueOf(3.75));

		return institution;
	}

	@Nonnull
	private KontaktAngabenDTO createKontaktAngabenDV() {
		KontaktAngabenDTO dto = new KontaktAngabenDTO();
		dto.setAnschrift("DV Bern AG");
		dto.setStrasse("Nussbaumstrasse");
		dto.setHausnummer("21");
		dto.setAdresszusatz("Postfach 106");
		dto.setPlz("CH-3000");
		dto.setOrt("Bern 22");
		dto.setLand("CH");
		dto.getGemeinde().setBfsNummer(351L);
		dto.getGemeinde().setName("Bern");
		dto.setWebseite("https://www.dvbern.ch/");
		dto.setTelefon("+41 31 378 24 24");
		dto.setEmail("hallo@dvbern.ch");

		return dto;
	}
}
