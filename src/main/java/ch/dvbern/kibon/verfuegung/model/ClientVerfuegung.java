package ch.dvbern.kibon.verfuegung.model;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.clients.model.ClientId;
import org.hibernate.annotations.Immutable;

@Table(indexes = @Index(name = "clientverfuegung_idx1", columnList = "clientname, since, id"))
@Entity
@Immutable
public class ClientVerfuegung {

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@Embedded
	private @NotNull @Valid ClientId clientId = new ClientId();

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "verfuegung_fk"), nullable = false, updatable = false)
	private @NotNull Verfuegung verfuegung = new Verfuegung();

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDateTime since = LocalDateTime.now();

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof ClientVerfuegung)) {
			return false;
		}

		ClientVerfuegung that = (ClientVerfuegung) o;

		return getId() != -1L &&
			getId().equals(that.getId()) &&
			getClientId().equals(that.getClientId()) &&
			getVerfuegung().equals(that.getVerfuegung());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClientId());
	}

	@Nonnull
	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Nonnull
	public ClientId getClientId() {
		return clientId;
	}

	public void setClientId(@Nonnull ClientId clientId) {
		this.clientId = clientId;
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
