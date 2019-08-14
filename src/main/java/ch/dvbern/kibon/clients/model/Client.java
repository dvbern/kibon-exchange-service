package ch.dvbern.kibon.clients.model;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Table(
	uniqueConstraints = @UniqueConstraint(name = "client_uc1", columnNames = { "clientId", "institutionId" }),
	indexes = @Index(name = "client_idx1", columnList = "clientId, institutionId, grantedSince")
)
@Entity
public class Client {

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
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDateTime grantedSince = LocalDateTime.MIN;

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
	public LocalDateTime getGrantedSince() {
		return grantedSince;
	}

	public void setGrantedSince(@Nonnull LocalDateTime grantedSince) {
		this.grantedSince = grantedSince;
	}
}
