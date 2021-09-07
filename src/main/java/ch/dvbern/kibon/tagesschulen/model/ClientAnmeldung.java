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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import org.hibernate.annotations.Immutable;

@Table(indexes = @Index(name = "clientanmeldung_idx1", columnList = "client_clientname, active, id"))
@Entity
@Immutable
public class ClientAnmeldung {

	private static final int HASH_CODE = 35;

	@Nonnull
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientanmeldung_generator")
	@SequenceGenerator(name = "clientanmeldung_generator", sequenceName = "clientanmeldung_id_seq")
	@Column(updatable = false, nullable = false)
	private @NotNull Long id = -1L;

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumns(foreignKey = @ForeignKey(name = "client_fk"),
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

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof ClientAnmeldung)) {
			return false;
		}

		ClientAnmeldung that = (ClientAnmeldung) o;

		return getId() != -1L
			&& getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return HASH_CODE;
	}

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
