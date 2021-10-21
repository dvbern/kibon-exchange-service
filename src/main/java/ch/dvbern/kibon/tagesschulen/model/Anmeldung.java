/*
 * Copyright (C) 2021 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.tagesschulen.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.tagesschulen.AbholungTagesschule;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungStatus;
import ch.dvbern.kibon.shared.model.AbstractInstitutionPeriodeEntity;
import ch.dvbern.kibon.util.ComparatorUtil;
import ch.dvbern.kibon.util.JsonNodeComparator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import org.hibernate.annotations.Type;

@Table(indexes = {
	@Index(name = "anmeldung_idx1", columnList = "institutionId"),
	@Index(name = "anmeldung_idx2", columnList = "refnr")
})
@Entity
public class Anmeldung extends AbstractInstitutionPeriodeEntity {

	// all properties, which when changed should result in a seperatly persisted Anmeldung
	public static final Comparator<Anmeldung> COMPARATOR = ComparatorUtil.<Anmeldung>baseComparator()
		.thenComparing(Anmeldung::getFreigegebenAm)
		.thenComparing(Anmeldung::getAnmeldungZurueckgezogen)
		.thenComparing(Anmeldung::getEintrittsdatum)
		.thenComparing(Anmeldung::getPlanKlasse, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(Anmeldung::getAbholung, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(Anmeldung::getAbweichungZweitesSemester)
		.thenComparing(Anmeldung::getBemerkung, Comparator.nullsLast(Comparator.naturalOrder()))
		.thenComparing(Anmeldung::getVersion)
		.thenComparing(Anmeldung::getKind, JsonNodeComparator.INSTANCE)
		.thenComparing(Anmeldung::getGesuchsteller, JsonNodeComparator.INSTANCE)
		.thenComparing(Anmeldung::getGesuchsteller2, Comparator.nullsLast(JsonNodeComparator.INSTANCE))
		.thenComparing(Anmeldung::getModule, JsonNodeComparator.INSTANCE);

	@Nonnull
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = false, updatable = false)
	private @NotNull JsonNode kind;

	@Nonnull
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = false, updatable = false)
	private @NotNull JsonNode gesuchsteller;

	@Nullable
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = true, updatable = false)
	private JsonNode gesuchsteller2;

	@Nonnull
	@Column(nullable = false)
	private @NotNull LocalDate freigegebenAm;

	@JsonIgnore
	@Nonnull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private @NotNull TagesschuleAnmeldungStatus status;

	@NotNull
	private boolean anmeldungZurueckgezogen;

	@Nonnull
	@Column(nullable = false)
	private @NotNull LocalDate eintrittsdatum;

	@Nullable
	private String planKlasse;

	@JsonIgnore
	@Nullable
	@Enumerated(EnumType.STRING)
	private AbholungTagesschule abholung;

	private boolean abweichungZweitesSemester;

	@Nullable
	private String bemerkung;

	@Nonnull
	@Column(updatable = false)
	private @NotNull LocalDateTime eventTimestamp = LocalDateTime.now();

	@Nonnull
	@Column(nullable = false)
	private @Min(0) Integer version = -1;

	@Nonnull
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = false, updatable = false)
	private @NotNull JsonNode module;

	@Nullable
	@Type(type = JsonTypes.JSON_OBJECT_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN, nullable = true, updatable = true)
	private JsonNode tarife;

	@SuppressWarnings("checkstyle:CyclomaticComplexity")
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Anmeldung)) {
			return false;
		}

		Anmeldung anmeldung = (Anmeldung) o;

		return super.equals(o)
			&& getAnmeldungZurueckgezogen() == anmeldung.getAnmeldungZurueckgezogen()
			&& getAbweichungZweitesSemester() == anmeldung.getAbweichungZweitesSemester()
			&& getFreigegebenAm().equals(anmeldung.getFreigegebenAm())
			&& getStatus() == anmeldung.getStatus()
			&& getEintrittsdatum().equals(anmeldung.getEintrittsdatum())
			&& Objects.equals(getPlanKlasse(), anmeldung.getPlanKlasse())
			&& getAbholung() == anmeldung.getAbholung()
			&& Objects.equals(getBemerkung(), anmeldung.getBemerkung())
			&& getEventTimestamp().equals(anmeldung.getEventTimestamp())
			&& getVersion().equals(anmeldung.getVersion());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			Arrays.hashCode(baseHashCodeValues()),
			getFreigegebenAm(),
			getStatus(),
			getAnmeldungZurueckgezogen(),
			getEintrittsdatum(),
			getPlanKlasse(),
			getAbholung(),
			getAbweichungZweitesSemester(),
			getBemerkung(),
			getEventTimestamp(),
			getVersion());
	}

	@Nonnull
	public JsonNode getKind() {
		return kind;
	}

	public void setKind(@Nonnull JsonNode kind) {
		this.kind = kind;
	}

	@Nonnull
	public JsonNode getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(@Nonnull JsonNode gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
	}

	@Nullable
	public JsonNode getGesuchsteller2() {
		return gesuchsteller2;
	}

	public void setGesuchsteller2(@Nullable JsonNode gesuchsteller2) {
		this.gesuchsteller2 = gesuchsteller2;
	}

	@Nonnull
	public LocalDate getFreigegebenAm() {
		return freigegebenAm;
	}

	public void setFreigegebenAm(@Nonnull LocalDate freigegebenAm) {
		this.freigegebenAm = freigegebenAm;
	}

	@Nonnull
	public TagesschuleAnmeldungStatus getStatus() {
		return status;
	}

	public void setStatus(@Nonnull TagesschuleAnmeldungStatus status) {
		this.status = status;
	}

	public boolean getAnmeldungZurueckgezogen() {
		return anmeldungZurueckgezogen;
	}

	public void setAnmeldungZurueckgezogen(boolean anmeldungZurueckgezogen) {
		this.anmeldungZurueckgezogen = anmeldungZurueckgezogen;
	}

	@Nonnull
	public LocalDate getEintrittsdatum() {
		return eintrittsdatum;
	}

	public void setEintrittsdatum(@Nonnull LocalDate eintrittsdatum) {
		this.eintrittsdatum = eintrittsdatum;
	}

	@Nullable
	public String getPlanKlasse() {
		return planKlasse;
	}

	public void setPlanKlasse(@Nullable String planKlasse) {
		this.planKlasse = planKlasse;
	}

	@Nullable
	public AbholungTagesschule getAbholung() {
		return abholung;
	}

	public void setAbholung(@Nullable AbholungTagesschule abholung) {
		this.abholung = abholung;
	}

	public boolean getAbweichungZweitesSemester() {
		return abweichungZweitesSemester;
	}

	public void setAbweichungZweitesSemester(boolean abweichungZweitesSemester) {
		this.abweichungZweitesSemester = abweichungZweitesSemester;
	}

	@Nullable
	public String getBemerkung() {
		return bemerkung;
	}

	public void setBemerkung(@Nullable String bemerkung) {
		this.bemerkung = bemerkung;
	}

	@Nonnull
	public LocalDateTime getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(@Nonnull LocalDateTime eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	@Nonnull
	public Integer getVersion() {
		return version;
	}

	public void setVersion(@Nonnull Integer version) {
		this.version = version;
	}

	@Nonnull
	public JsonNode getModule() {
		return module;
	}

	public void setModule(@Nonnull JsonNode anmeldungModule) {
		this.module = anmeldungModule;
	}

	@Nullable
	public JsonNode getTarife() {
		return tarife;
	}

	public void setTarife(@Nullable JsonNode tarife) {
		this.tarife = tarife;
	}
}
