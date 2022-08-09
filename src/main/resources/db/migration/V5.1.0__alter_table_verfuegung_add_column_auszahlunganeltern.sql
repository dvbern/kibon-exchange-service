ALTER TABLE verfuegung
    ADD COLUMN auszahlunganeltern BOOLEAN DEFAULT FALSE;

ALTER TABLE verfuegung
    ALTER COLUMN auszahlunganeltern DROP DEFAULT;
