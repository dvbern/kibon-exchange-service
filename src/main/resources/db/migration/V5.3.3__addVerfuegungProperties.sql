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

ALTER TABLE verfuegung
	ADD COLUMN mandant VARCHAR(100) NOT NULL DEFAULT 'BERN';

UPDATE verfuegung
SET mandant = 'LUZERN'
WHERE gemeindebfsnr = 1061;

UPDATE verfuegung
SET mandant = 'SOLOTHURN'
WHERE gemeindebfsnr >= 2401 AND gemeindebfsnr <= 2622;

CREATE INDEX verfuegung_mandant_idx
    ON verfuegung(mandant);
