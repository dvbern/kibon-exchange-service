package ch.dvbern.kibon.clients.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Table(indexes = @Index(name = "client_idx1", columnList = "clientname, institutionId, grantedSince"))
@Entity
public class Client {

	@Nonnull
	@EmbeddedId
	private @NotNull @Valid ClientId id = new ClientId();

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDateTime grantedSince = LocalDateTime.MIN;

	@Nonnull
	@Column(nullable = false, updatable = true)
	private @NotNull Boolean active = true;

	public Client() {
	}

	public Client(@Nonnull ClientId id, @Nonnull LocalDateTime grantedSince) {
		this.id = id;
		this.grantedSince = grantedSince;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Client)) {
			return false;
		}

		Client client = (Client) o;

		return getId().equals(client.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	@Nonnull
	public String toString() {
		return new StringJoiner(", ", Client.class.getSimpleName() + '[', "]")
			.add("id=" + id)
			.add("grantedSince=" + grantedSince)
			.add("active=" + active)
			.toString();
	}

	@Nonnull
	public ClientId getId() {
		return id;
	}

	public void setId(@Nonnull ClientId id) {
		this.id = id;
	}

	@Nonnull
	public LocalDateTime getGrantedSince() {
		return grantedSince;
	}

	public void setGrantedSince(@Nonnull LocalDateTime grantedSince) {
		this.grantedSince = grantedSince;
	}

	@Nonnull
	public Boolean getActive() {
		return active;
	}

	public void setActive(@Nonnull Boolean active) {
		this.active = active;
	}
}
