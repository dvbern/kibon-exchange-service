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

package ch.dvbern.kibon.clients.model;

import java.time.LocalDate;
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

import ch.dvbern.kibon.persistence.BaseEntity;

/**
 * Information regarding a kibon-exchange client (kitAdmin etc.).<br>
 */
@Table(indexes = @Index(name = "client_idx1", columnList = "clientname, institutionId, grantedSince"))
@Entity
public class Client extends BaseEntity {

	@Nonnull
	@EmbeddedId
	private @NotNull @Valid ClientId id = new ClientId();

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotNull LocalDateTime grantedSince = LocalDateTime.MIN;

	@Nonnull
	@Column(nullable = false, updatable = true)
	private @NotNull Boolean active = true;

	@Nullable
	@Column(nullable = true, updatable = true)
	private LocalDate gueltigAb;

	@Nullable
	@Column(nullable = true, updatable = true)
	private LocalDate gueltigBis;

	public Client() {
	}

	public Client(
		@Nonnull ClientId id,
		@Nonnull LocalDateTime grantedSince,
		@Nullable LocalDate gueltigAb,
		@Nullable LocalDate gueltigBis) {
		this.id = id;
		this.grantedSince = grantedSince;
		this.gueltigAb = gueltigAb;
		this.gueltigBis = gueltigBis;
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
			.add("gueltigAb=" + getGueltigAb())
			.add("gueltigBis=" + getGueltigBis())
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

	@Nullable
	public LocalDate getGueltigAb() {
		return gueltigAb;
	}

	public void setGueltigAb(@Nullable LocalDate gueltigAb) {
		this.gueltigAb = gueltigAb;
	}

	@Nullable
	public LocalDate getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(@Nullable LocalDate gueltigBis) {
		this.gueltigBis = gueltigBis;
	}
}
