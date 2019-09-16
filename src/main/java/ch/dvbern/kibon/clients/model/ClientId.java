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

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

import ch.dvbern.kibon.institution.model.Institution;
import ch.dvbern.kibon.verfuegung.model.ClientVerfuegung;
import ch.dvbern.kibon.verfuegung.model.Verfuegung;

/**
 * <p>A {@link Client}s primary key.</p>
 *
 * <p>clientName is the name used by keycloak. As such, having a generated numeric PK insrtead
 * would not provide much benefit, because we have to look up / filter users by name anyways.</p>
 *
 * <p>The institution is part of the PK because of performance reasons.
 * We aim for fast searching even when filtering by institution.
 * As such, we want to avoid having to join to the {@link Institution} table just to be able to access/filter by
 * institution.</p>
 */
@Embeddable
public class ClientId implements Serializable {

	private static final long serialVersionUID = 4592863479222722626L;

	@Nonnull
	@Column(nullable = false, updatable = false)
	private @NotEmpty String clientName = "";

	/**
	 * No FK because we cannot guarantee, that the institution will be created in kibon-exchange before the client.
	 */
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
