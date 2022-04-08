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

package ch.dvbern.kibon.betreuung.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.clients.model.Client;
import ch.dvbern.kibon.shared.model.AbstractClientEntity;
import org.hibernate.annotations.Immutable;

/**
 * <p>This table Contains an entry for every combination of a verfuegung and a client.</p>
 * <p>It is meant for fast {@link BetreuungAnfrage} lookup. As such, we have an active flag to avoid having to join
 * with the {@link Client} table. Furher we get the client keycloak name and the institution for filtering from the
 * linked Client entries, again allowing filtering without any joins.</p>
 */
@SequenceGenerator(name = "id_generator", sequenceName = ClientBetreuungAnfrage.ID_SEQUENCE)
@Table(indexes = @Index(name = "clientbetreuunganfrage_idx1", columnList = "client_clientname, active, id"))
@Entity
@Immutable
public class ClientBetreuungAnfrage extends AbstractClientEntity {

	public static final String ID_SEQUENCE = "clientbetreuunganfrage_id_seq";

	private static final int HASH_CODE = 33;

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "betreuunganfrage_fk"), nullable = false, updatable = false)
	private @NotNull BetreuungAnfrage betreuungAnfrage = new BetreuungAnfrage();

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof ClientBetreuungAnfrage)) {
			return false;
		}

		ClientBetreuungAnfrage that = (ClientBetreuungAnfrage) o;

		return getId() != -1L &&
			getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return HASH_CODE;
	}

	@Nonnull
	public BetreuungAnfrage getBetreuungAnfrage() {
		return betreuungAnfrage;
	}

	public void setBetreuungAnfrage(@Nonnull BetreuungAnfrage betreuungAnfrage) {
		this.betreuungAnfrage = betreuungAnfrage;
	}
}
