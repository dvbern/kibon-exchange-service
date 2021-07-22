package ch.dvbern.kibon.tagesschulen.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.exchange.commons.tagesschulen.AbholungTagesschule;
import ch.dvbern.kibon.exchange.commons.tagesschulen.TagesschuleAnmeldungStatus;
import ch.dvbern.kibon.shared.model.Gesuchsperiode;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;

@Entity
public class Anmeldung {

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode kind;

	@Nonnull
	@Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb", nullable = false, updatable = false)
	private @NotNull JsonNode gesuchsteller;

	@Nonnull
	@Column(nullable = false)
	private @NotNull LocalDate freigegebenAm;

	@Nonnull
	@Column(nullable = false)
	private @NotNull TagesschuleAnmeldungStatus status;

	@NotNull
	private boolean anmeldungZurueckgezogen;

	@Nonnull
	@Column(nullable = false)
	private @NotNull String refnr;

	@Nonnull
	@Column(nullable = false)
	private  @NotNull LocalDate eintrittsdatum;

	@Nullable
	private String planKlasse;

	@Nullable
	private AbholungTagesschule abholung;

	@NotNull
	private boolean abweichungZweitesSemester;

	@Nonnull
	@Column(nullable = false)
	private @NotNull String bemerkung;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "anmeldung_gesuchsperiode_fk"), nullable = false, updatable = false)
	private Gesuchsperiode gesuchsperiode;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotEmpty String institutionId = "";

	@Nonnull
	@Column(updatable = false)
	private @NotNull LocalDateTime eventTimestamp = LocalDateTime.now();

	@NotNull
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "anmeldung")
	@SortNatural
	private @Valid Set<AnmeldungModul> anmeldungModulSet = new TreeSet<>();


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	public String getRefnr() {
		return refnr;
	}

	public void setRefnr(@Nonnull String refnr) {
		this.refnr = refnr;
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

	@Nonnull
	public String getBemerkung() {
		return bemerkung;
	}

	public void setBemerkung(@Nonnull String bemerkung) {
		this.bemerkung = bemerkung;
	}

	public Gesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	@Nonnull
	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(@Nonnull String institutionId) {
		this.institutionId = institutionId;
	}

	@Nonnull
	public LocalDateTime getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(@Nonnull LocalDateTime eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public Set<AnmeldungModul> getAnmeldungModulSet() {
		return anmeldungModulSet;
	}

	public void setAnmeldungModulSet(Set<AnmeldungModul> anmeldungModulSet) {
		this.anmeldungModulSet = anmeldungModulSet;
	}
}
