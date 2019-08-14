package ch.dvbern.kibon.verfuegung.model;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

@Table(indexes = @Index(name = "client_verfuegung_idx1", columnList = "clientid, since, id"))
@Entity
@Immutable
public class ClientVerfuegung {

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotEmpty String clientId = "";

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotEmpty String institutionId = "";

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "verfuegung_fk"), nullable = false, updatable = false)
	private @NotNull Verfuegung verfuegung = new Verfuegung();

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDateTime since = LocalDateTime.now();

	@Nonnull
	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Nonnull
	public String getClientId() {
		return clientId;
	}

	public void setClientId(@Nonnull String clientId) {
		this.clientId = clientId;
	}

	@Nonnull
	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(@Nonnull String institutionId) {
		this.institutionId = institutionId;
	}

	@Nonnull
	public Verfuegung getVerfuegung() {
		return verfuegung;
	}

	public void setVerfuegung(@Nonnull Verfuegung verfuegung) {
		this.verfuegung = verfuegung;
	}

	@Nonnull
	public LocalDateTime getSince() {
		return since;
	}

	public void setSince(@Nonnull LocalDateTime since) {
		this.since = since;
	}
}
