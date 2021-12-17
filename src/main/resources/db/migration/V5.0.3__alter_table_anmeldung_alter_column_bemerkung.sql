ALTER TABLE anmeldung
    ALTER COLUMN bemerkung TYPE TEXT USING bemerkung::TEXT;
