package ch.dvbern.kibon.tagesschulen.model;

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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.clients.model.Client;

@Table(indexes = @Index(name = "clientanmeldung_idx1", columnList = "client_clientname, active, id"))
@Entity
public class ClientAnmeldung {

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientanmeldung_generator")
	@SequenceGenerator(name = "clientanmeldung_generator", sequenceName = "clientanmeldung_id_seq")
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns(foreignKey = @ForeignKey(name = "client_anmeldung_fk"),
		value = {
			@JoinColumn(nullable = false, updatable = false),
			@JoinColumn(nullable = false, updatable = false)
		})
	private @NotNull Client client = new Client();

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "client_anmeldung_anmeldung_fk"), nullable = false, updatable = false)
	private @NotNull Anmeldung anmeldung = new Anmeldung();

	@Nonnull
	@Column(nullable = false, updatable = true)
	private @NotNull Boolean active = true;

	@Nonnull
	public Long getId() {
		return id;
	}

	public void setId(@Nonnull Long id) {
		this.id = id;
	}

	@Nonnull
	public Client getClient() {
		return client;
	}

	public void setClient(@Nonnull Client client) {
		this.client = client;
	}

	@Nonnull
	public Anmeldung getAnmeldung() {
		return anmeldung;
	}

	public void setAnmeldung(@Nonnull Anmeldung anmeldung) {
		this.anmeldung = anmeldung;
	}

	@Nonnull
	public Boolean getActive() {
		return active;
	}

	public void setActive(@Nonnull Boolean active) {
		this.active = active;
	}
}
