package ch.dvbern.kibon.tagesschulen.model;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.types.ModulIntervall;
import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.shared.model.Gesuchsperiode;
import ch.dvbern.kibon.util.ConstantsUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

@Entity
public class Modul implements Comparable<Modul> {

	@Id
	@NotNull
	private @NotEmpty String id = "";

	@NotNull
	private @NotEmpty String bezeichnungDE;

	@NotNull
	private @NotEmpty String bezeichnungFR;

	@NotNull
	private LocalTime zeitVon;

	@NotNull
	private LocalTime zeitBis;

	/**
	 * A {@link java.util.List<ch.dvbern.kibon.exchange.commons.types.Wochentag>}
	 */
	@SuppressWarnings("UnnecessaryFullyQualifiedName")
	@NotNull
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb")
	private JsonNode wochentage;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(length = ConstantsUtil.SHORT_COLUMN_SIZE)
	private ModulIntervall intervall;

	@NotNull
	private boolean padaegogischBetreut;

	@NotNull
	private BigDecimal verpflegungsKosten;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "gesuchsperiode_fk"), nullable = false, updatable = false)
	private Gesuchsperiode gesuchsperiode;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "institution_fk"), nullable = false, updatable = false)
	private Institution institution;

	public Modul() {
	}

	/**
	 * For test
	 * @param modulId
	 */
	public Modul(String modulId) {
		id = modulId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBezeichnungDE() {
		return bezeichnungDE;
	}

	public void setBezeichnungDE(String bezeichnungDE) {
		this.bezeichnungDE = bezeichnungDE;
	}

	public String getBezeichnungFR() {
		return bezeichnungFR;
	}

	public void setBezeichnungFR(String bezeichnungFR) {
		this.bezeichnungFR = bezeichnungFR;
	}

	public LocalTime getZeitVon() {
		return zeitVon;
	}

	public void setZeitVon(LocalTime zeitVon) {
		this.zeitVon = zeitVon;
	}

	public LocalTime getZeitBis() {
		return zeitBis;
	}

	public void setZeitBis(LocalTime zeitBis) {
		this.zeitBis = zeitBis;
	}

	public JsonNode getWochentage() {
		return wochentage;
	}

	public void setWochentage(JsonNode wochentage) {
		this.wochentage = wochentage;
	}

	public ModulIntervall getIntervall() {
		return intervall;
	}

	public void setIntervall(ModulIntervall intervall) {
		this.intervall = intervall;
	}

	public boolean isPadaegogischBetreut() {
		return padaegogischBetreut;
	}

	public void setPadaegogischBetreut(boolean padaegogischBetreut) {
		this.padaegogischBetreut = padaegogischBetreut;
	}

	public BigDecimal getVerpflegungsKosten() {
		return verpflegungsKosten;
	}

	public void setVerpflegungsKosten(BigDecimal verpflegungsKosten) {
		this.verpflegungsKosten = verpflegungsKosten;
	}

	public Gesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Modul)) {
			return false;
		}

		Modul that = (Modul) o;

		return !getId().isEmpty() &&
			getId().equals(that.getId()) &&
			getBezeichnungDE().equals(that.getBezeichnungDE()) &&
			getBezeichnungFR().equals(that.getBezeichnungFR()) &&
			Objects.equals(getGesuchsperiode(), that.getGesuchsperiode()) &&
			Objects.equals(getInstitution(), that.getInstitution());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getBezeichnungDE(), getBezeichnungFR());
	}

	@Override
	public int compareTo(Modul o) {
		int compare = getZeitVon().compareTo(o.zeitVon);
		if(compare == 0) {
			compare = getZeitBis().compareTo(o.zeitBis);
		}
		if(compare == 0) {
			compare = getId().compareTo(o.getId());
		}
		if(compare == 0) {
			compare = getBezeichnungDE().compareTo(o.getBezeichnungDE());
		}
		if(compare == 0) {
			compare = getBezeichnungFR().compareTo(o.getBezeichnungFR());
		}
		if(compare == 0) {
			compare = getVerpflegungsKosten().compareTo(o.getVerpflegungsKosten());
		}
		if(compare == 0) {
			compare = getIntervall().compareTo(o.getIntervall());
		}
		if(compare == 0) {
			compare = getWochentage().equals(o.getWochentage()) ? 0 : 1;
		}
		return compare;
	}
}
