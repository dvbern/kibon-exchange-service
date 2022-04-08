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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ch.dvbern.kibon.shared.model.AbstractClientEntity;
import org.hibernate.annotations.Immutable;

@SequenceGenerator(name = "id_generator", sequenceName = ClientAnmeldung.ID_SEQUENCE)
@Table(indexes = @Index(name = "clientanmeldung_idx1", columnList = "client_clientname, active, id"))
@Entity
@Immutable
public class ClientAnmeldung extends AbstractClientEntity {

	public static final String ID_SEQUENCE = "clientanmeldung_id_seq";

	private static final int HASH_CODE = 35;

	@Nonnull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "client_anmeldung_anmeldung_fk"), nullable = false, updatable = false)
	private @NotNull Anmeldung anmeldung = new Anmeldung();

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
	public Anmeldung getAnmeldung() {
		return anmeldung;
	}

	public void setAnmeldung(@Nonnull Anmeldung anmeldung) {
		this.anmeldung = anmeldung;
	}
}
