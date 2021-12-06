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

package ch.dvbern.kibon.institution.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ch.dvbern.kibon.exchange.commons.institution.InstitutionEventDTO;
import ch.dvbern.kibon.exchange.commons.institution.KontaktAngabenDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.ModulDTO;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleModuleDTO;
import ch.dvbern.kibon.exchange.commons.util.TimeConverter;
import ch.dvbern.kibon.exchange.commons.util.TimestampConverter;
import ch.dvbern.kibon.institution.model.Gemeinde;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.institution.model.KontaktAngaben;
import ch.dvbern.kibon.tagesschulen.model.Modul;
import ch.dvbern.kibon.tagesschulen.model.TagesschuleModule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class InstitutionConverter {

	@SuppressWarnings("checkstyle:VisibilityModifier")
	@Inject
	ObjectMapper mapper;

	@Nonnull
	public Institution create(@Nonnull InstitutionEventDTO dto) {
		Institution institution = new Institution();
		institution.setId(dto.getId());
		update(institution, dto);

		return institution;
	}

	public void update(@Nonnull Institution institution, @Nonnull InstitutionEventDTO dto) {
		institution.setName(dto.getName());
		institution.setTraegerschaft(dto.getTraegerschaft());
		if (dto.getBetreuungsArt() != null) {
			institution.setBetreuungsArt(dto.getBetreuungsArt());
		}
		if (dto.getStatus() != null) {
			institution.setStatus(dto.getStatus());
		}
		institution.setBetreuungsGutscheineAb(dto.getBetreuungsGutscheineAb());
		institution.setBetreuungsGutscheineBis(dto.getBetreuungsGutscheineBis());

		update(institution.getKontaktAdresse(), dto.getAdresse());
		institution.setBetreuungsAdressen(toBetreuungsStandorte(dto.getBetreuungsAdressen()));

		institution.setOeffnungsTage(mapper.valueToTree(dto.getOeffnungsTage()));
		institution.setOffenVon(TimeConverter.deserialize(dto.getOffenVon()));
		institution.setOffenBis(TimeConverter.deserialize(dto.getOffenBis()));
		institution.setOeffnungsAbweichungen(dto.getOeffnungsAbweichungen());

		institution.setAltersKategorien(mapper.valueToTree(dto.getAltersKategorien()));
		institution.setSubventioniertePlaetze(dto.getSubventioniertePlaetze());
		institution.setAnzahlPlaetze(dto.getAnzahlPlaetze());
		institution.setAnzahlPlaetzeFirmen(dto.getAnzahlPlaetzeFirmen());
		if (dto.getTimestampMutiert() != null) {
			institution.setTimestampMutiert(TimestampConverter.toLocalDateTime(dto.getTimestampMutiert()));
		}

		if (dto.getTagesschuleModule() != null) {
			update(institution, dto.getTagesschuleModule());
		}
	}

	private void update(@Nonnull KontaktAngaben adresse, @Nonnull KontaktAngabenDTO dto) {
		adresse.setAnschrift(dto.getAnschrift());
		adresse.setStrasse(dto.getStrasse());
		adresse.setHausnummer(dto.getHausnummer());
		adresse.setAdresszusatz(dto.getAdresszusatz());
		adresse.setOrt(dto.getOrt());
		adresse.setPlz(dto.getPlz());
		adresse.setLand(dto.getLand());
		adresse.setGemeinde(getGemeinde(dto));
		adresse.setEmail(dto.getEmail());
		adresse.setTelefon(dto.getTelefon());
		adresse.setWebseite(dto.getWebseite());
	}

	@Nonnull
	private Gemeinde getGemeinde(@Nonnull KontaktAngabenDTO dto) {
		Gemeinde gemeinde = new Gemeinde();
		gemeinde.setName(dto.getGemeinde().getName());
		gemeinde.setBfsNummer(dto.getGemeinde().getBfsNummer());

		return gemeinde;
	}

	@Nonnull
	private JsonNode toBetreuungsStandorte(@Nullable List<KontaktAngabenDTO> kontaktAngaben) {
		if (kontaktAngaben == null) {
			return mapper.createArrayNode();
		}

		List<ObjectNode> mapped = kontaktAngaben.stream()
			.map(this::toKontaktAngaben)
			.collect(Collectors.toList());

		return mapper.createArrayNode()
			.addAll(mapped);
	}

	@Nonnull
	private ObjectNode toKontaktAngaben(@Nonnull KontaktAngabenDTO dto) {
		ObjectNode result = mapper.createObjectNode()
			.put("anschrift", dto.getAnschrift())
			.put("strasse", dto.getStrasse())
			.put("hausnummer", dto.getHausnummer())
			.put("adresszusatz", dto.getAdresszusatz())
			.put("plz", dto.getPlz())
			.put("ort", dto.getOrt())
			.put("land", dto.getLand())
			.put("email", dto.getEmail())
			.put("telefon", dto.getTelefon())
			.put("webseite", dto.getWebseite());

		result.putObject("gemeinde")
			.put("name", dto.getGemeinde().getName())
			.put("bfsNummer", dto.getGemeinde().getBfsNummer());

		return result;
	}

	private void update(@Nonnull Institution institution, @Nonnull List<TagesschuleModuleDTO> tagesschuleModule) {
		// at the moment, this is only appending & updating new TagesschuleModule.
		// There is no use case to remove TagesschuleModule, since there might be existing Anmeldungen
		List<TagesschuleModule> convertedModule = tagesschuleModule.stream()
			.map(dtoT -> toTageschuleModule(institution, dtoT))
			.collect(Collectors.toList());

		institution.getTagesschuleModule().addAll(convertedModule);
	}

	@Nonnull
	private TagesschuleModule toTageschuleModule(@Nonnull Institution institution, @Nonnull TagesschuleModuleDTO dto) {
		TagesschuleModule entity = getOrCreateTagesschuleModule(institution, dto);

		Set<Modul> existingModule = new HashSet<>(entity.getModule());

		List<Modul> module = dto.getModule().stream()
			.map(m -> getOrCreateModul(existingModule, m))
			.collect(Collectors.toList());

		module.forEach(m -> m.setParent(entity));
		entity.getModule().clear();
		entity.getModule().addAll(module);

		return entity;
	}

	@Nonnull
	private TagesschuleModule getOrCreateTagesschuleModule(
		@Nonnull Institution institution,
		@Nonnull TagesschuleModuleDTO dto) {

		return institution.getTagesschuleModule().stream()
			.filter(t -> t.getPeriodeVon().equals(dto.getPeriodeVon()) && t.getPeriodeBis().equals(dto.getPeriodeBis()))
			.findAny()
			.orElseGet(() -> createTagesschuleModule(institution, dto));
	}

	@Nonnull
	private TagesschuleModule createTagesschuleModule(
		@Nonnull Institution institution,
		@Nonnull TagesschuleModuleDTO dto) {

		TagesschuleModule tagesschuleModule = new TagesschuleModule();
		tagesschuleModule.setInstitution(institution);
		tagesschuleModule.setPeriodeVon(dto.getPeriodeVon());
		tagesschuleModule.setPeriodeBis(dto.getPeriodeBis());

		return tagesschuleModule;
	}

	@Nonnull
	private Modul getOrCreateModul(@Nonnull Set<Modul> existingModule, @Nonnull ModulDTO modulDTO) {
		Modul modul = existingModule.stream()
			.filter(e -> e.getId().equals(modulDTO.getId()))
			.findAny()
			.orElseGet(() -> newModul(modulDTO));

		updateModul(modul, modulDTO);

		return modul;
	}

	@Nonnull
	private Modul newModul(@Nonnull ModulDTO modulDTO) {
		Modul modul = new Modul();
		modul.setId(modulDTO.getId());

		return modul;
	}

	private void updateModul(@Nonnull Modul modul, @Nonnull ModulDTO modulDTO) {
		modul.setBezeichnungDE(modulDTO.getBezeichnungDE());
		modul.setBezeichnungFR(modulDTO.getBezeichnungFR());
		modul.setErlaubteIntervalle(mapper.valueToTree(modulDTO.getErlaubteIntervalle()));
		modul.setWochentage(mapper.valueToTree(modulDTO.getWochentage()));
		modul.setWirdPaedagogischBetreut(modulDTO.getWirdPaedagogischBetreut());
		modul.setVerpflegungsKosten(modulDTO.getVerpflegungsKosten());
		modul.setZeitVon(TimeConverter.deserialize(modulDTO.getZeitVon()));
		modul.setZeitBis(TimeConverter.deserialize(modulDTO.getZeitBis()));
		modul.setFremdId(modulDTO.getFremdId());
	}
}
