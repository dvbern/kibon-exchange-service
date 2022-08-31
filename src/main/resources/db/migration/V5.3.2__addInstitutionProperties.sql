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

UPDATE institution
SET mandant = 'LUZERN'
WHERE bfsnummer = 1061;

UPDATE institution
SET mandant = 'SOLOTHURN'
WHERE bfsnummer >= 2401 AND bfsnummer <= 2622;

ALTER TABLE gemeinde
    ADD COLUMN mandant VARCHAR(100) NOT NULL DEFAULT 'BERN';

ALTER TABLE gemeindekennzahlen
    ADD COLUMN mandant VARCHAR(100) NOT NULL DEFAULT 'BERN';

DELETE
FROM institution
WHERE id = 'f2491e9c-bd95-44df-b22f-f36a42f8592f';

CREATE INDEX gemeinde_mandant_idx
    ON gemeinde(mandant);

CREATE INDEX gemeindekennzahlen_mandant_idx
    ON gemeindekennzahlen(mandant);

CREATE INDEX institution_mandant_idx
    ON institution(mandant);

