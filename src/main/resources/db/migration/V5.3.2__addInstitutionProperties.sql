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

ALTER TABLE institution
    ADD COLUMN sequenceid BIGSERIAL NOT NULL;

ALTER TABLE institution
	ALTER COLUMN sequenceid SET DEFAULT nextval('institution_sequenceid_seq');

CREATE INDEX institution_sequenceid_idx
	ON institution(sequenceid);

ALTER TABLE institution
    ADD UNIQUE (sequenceid);

ALTER TABLE institution
	ADD COLUMN auslastungpct NUMERIC(19, 2);

ALTER TABLE institution
	ADD COLUMN mandant VARCHAR(100) NOT NULL DEFAULT 'BERN';

UPDATE institution set mandant = 'LUZERN' where bfsnummer = 1061;

UPDATE institution set mandant = 'SOLOTHURN' where bfsnummer >= 2401 and bfsnummer <= 2622;

ALTER TABLE gemeinde
	ADD COLUMN mandant VARCHAR(100) NOT NULL DEFAULT 'BERN';

ALTER TABLE gemeindekennzahlen
	ADD COLUMN mandant VARCHAR(100) NOT NULL DEFAULT 'BERN';DELETE FROM institution where id='f2491e9c-bd95-44df-b22f-f36a42f8592f';

CREATE INDEX gemeinde_mandant_idx
	ON gemeinde(mandant);

CREATE INDEX gemeindekennzahlen_mandant_idx
	ON gemeindekennzahlen(mandant);

CREATE INDEX institution_mandant_idx
	ON institution(mandant);

