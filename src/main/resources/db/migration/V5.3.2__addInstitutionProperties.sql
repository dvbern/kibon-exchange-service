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
	ADD COLUMN institutionid VARCHAR(255);

UPDATE institution set institutionid = id;

ALTER TABLE institution
	ALTER COLUMN institutionid VARCHAR(255) NOT NULL;

ALTER TABLE institution
	DROP PRIMARY KEY;

ALTER TABLE institution
    ADD COLUMN id BIGSERIAL NOT NULL
        CONSTRAINT institution_pkey
            PRIMARY KEY;

ALTER TABLE institution
	ADD COLUMN auslastungpct NUMERIC(19, 2);

CREATE INDEX institution_idx2 ON institution(institutionid);
