/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.verfuegung.model;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
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
 * <p>It is meant for fast {@link Verfuegung} lookup. As such, we have an active flag to avoid having to join with the
 * {@link Client} table. Furher we get the client keycloak name and the institution for filtering from the linked Client
 * entries, again allowing filtering without any joins.</p>
 */
@SequenceGenerator(name = "id_generator", sequenceName = ClientVerfuegung.ID_SEQUENCE)
@Table(indexes = @Index(name = "clientverfuegung_idx1", columnList = "client_clientname, active, since, id"))
@Entity
@Immutable
public class ClientVerfuegung extends AbstractClientEntity {

	public static final String ID_SEQUENCE = "clientverfuegung_id_seq";

	// The hashcode should always return the same value, regardless of entity state transitions.
	// Since equals only includes the auto-generated 'id', we must provide a constant value.
	// See https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
	private static final int HASH_CODE = 31;

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
			getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return HASH_CODE;
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
