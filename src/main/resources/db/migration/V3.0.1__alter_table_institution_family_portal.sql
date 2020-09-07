ALTER TABLE institution
	ADD COLUMN status                  VARCHAR(100) NOT NULL DEFAULT 'AKTIV',
	ADD COLUMN betreuungsgutscheineab  DATE,
	ADD COLUMN betreuungsgutscheinebis DATE;

ALTER TABLE institution
	ALTER COLUMN status DROP DEFAULT;

CREATE INDEX institution_idx1 ON institution(betreuungsart, status);
