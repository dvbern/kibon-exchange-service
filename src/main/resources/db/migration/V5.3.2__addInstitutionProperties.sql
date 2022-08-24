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

CREATE SEQUENCE institution_zusatzid_seq;

ALTER TABLE institution
    ADD COLUMN zusatzid BIGSERIAL NOT NULL;

ALTER TABLE institution
	ALTER COLUMN zusatzid SET DEFAULT nextval('institution_zusatzid_seq');

CREATE INDEX institution_zusatzid_idx
	ON institution(zusatzid);

ALTER TABLE institution
    ADD UNIQUE (zusatzid);

ALTER TABLE institution
	ADD COLUMN auslastungpct NUMERIC(19, 2);

ALTER TABLE institution
	ADD COLUMN mandant VARCHAR(10) NOT NULL DEFAULT 'BERN';

UPDATE institution set mandant = 'LUZERN' where bfsnummer = 1061;

UPDATE institution set mandant = 'SOLOTHURN' where bfsnummer >= 2401 and bfsnummer <= 2622;

ALTER TABLE gemeinde
	ADD COLUMN mandant VARCHAR(10) NOT NULL DEFAULT 'BERN';

ALTER TABLE gemeindekennzahlen
	ADD COLUMN mandant VARCHAR(10) NOT NULL DEFAULT 'BERN';


