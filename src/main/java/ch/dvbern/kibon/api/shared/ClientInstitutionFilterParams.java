/*
 * Copyright (C) 2022 DV Bern AG, Switzerland
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

package ch.dvbern.kibon.api.shared;

import java.util.StringJoiner;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import static ch.dvbern.kibon.util.ConstantsUtil.DEFAULT_LIMIT;
import static ch.dvbern.kibon.util.ConstantsUtil.MAX_LIMIT;

public class ClientInstitutionFilterParams {

	@Parameter(description = "Erlaubt es, nur neue Daten zu laden.\n\nJeder Datensatz hat eine "
		+ "monoton steigende ID. Ein Client kann deshalb die grösste ID seiner bereits eingelesener Daten als"
		+ " `after_id` Parameter setzen, um nur neuere Datensätze zu erhalten.")
	@QueryParam("after_id")
	@Nullable
	private Long afterId = null;

	@Parameter(description = "Beschränkt die maximale Anzahl Resultate auf den angeforderten Wert.")
	@Min(0)
	@Max(MAX_LIMIT)
	@QueryParam("limit")
	@DefaultValue(DEFAULT_LIMIT)
	@Nullable
	private Integer limit = null;

	@Parameter(description = "Liefert nur Resultate mit spezifischer Referenznummer")
	@QueryParam("refnr")
	@Nullable
	private String refnr = null;

	@Parameter(description = "Liefert nur Daten zu einer ausgewählten Institution.")
	@QueryParam("institution_id")
	@Nullable
	private String institutionId = null;

	@Override
	public String toString() {
		return new StringJoiner(", ", ClientInstitutionFilterParams.class.getSimpleName() + '[', "]")
			.add("afterId=" + afterId)
			.add("limit=" + limit)
			.add("refnr='" + refnr + '\'')
			.add("institutionId='" + institutionId + '\'')
			.toString();
	}

	@Nullable
	public Long getAfterId() {
		return afterId;
	}

	public void setAfterId(@Nullable Long afterId) {
		this.afterId = afterId;
	}

	@Nullable
	public Integer getLimit() {
		return limit;
	}

	public void setLimit(@Nullable Integer limit) {
		this.limit = limit;
	}

	@Nullable
	public String getRefnr() {
		return refnr;
	}

	public void setRefnr(@Nullable String refnr) {
		this.refnr = refnr;
	}

	@Nullable
	public String getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(@Nullable String institutionId) {
		this.institutionId = institutionId;
	}
}
