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

CREATE TABLE gemeindekennzahlen (
	id                            BIGSERIAL NOT NULL
		CONSTRAINT gemeindekennzahlen_pkey PRIMARY KEY,
	bfsnummer                     BIGINT    NOT NULL,
	gesuchsperiodestart           DATE      NOT NULL,
	gesuchsperiodestop            DATE      NOT NULL,
	kontingentierung              BOOLEAN,
	kontingentierungausgeschoepft BOOLEAN,
	anzahlKinderwarteliste        NUMERIC(19, 2),
	dauerwarteliste               NUMERIC(19, 2),
	erwerbspensumzuschlag         NUMERIC(19, 2),
	limitierungtfo                VARCHAR(255),
	limitierungkita               VARCHAR(255)
);
