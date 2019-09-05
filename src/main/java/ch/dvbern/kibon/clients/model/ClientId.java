package ch.dvbern.kibon.clients.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Embeddable
public class ClientId implements Serializable {

	private static final long serialVersionUID = 4592863479222722626L;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotEmpty String clientName = "";

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotEmpty String institutionId = "";

	public ClientId() {
	}

	public ClientId(@Nonnull String clientName, @Nonnull String institutionId) {
		this.clientName = clientName;
		this.institutionId = institutionId;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof ClientId)) {
			return false;
		}

		ClientId clientId = (ClientId) o;

		return getClientName().equals(clientId.getClientName()) &&
			getInstitutionId().equals(clientId.getInstitutionId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClientName(), getInstitutionId());
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", ClientId.class.getSimpleName() + '[', "]")
			.add("clientName='" + clientName + '\'')
			.add("institutionId='" + institutionId + '\'')
			.toString();
	}

	@Nonnull
	public String getClientName() {
		return clientName;
	}

	public void setClientName(@Nonnull String clientName) {
		this.clientName = clientName;
	}

	@Nonnull
	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(@Nonnull String institutionId) {
		this.institutionId = institutionId;
	}
}
