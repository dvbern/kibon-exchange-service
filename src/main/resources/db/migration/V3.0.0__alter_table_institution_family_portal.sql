ALTER TABLE institution
	ADD COLUMN betreuungsart          VARCHAR(100) NOT NULL DEFAULT 'KITA',
	ADD COLUMN anschrift              VARCHAR(255),
	ADD COLUMN bfsnummer              BIGINT,
	ADD COLUMN gemeinde_name          VARCHAR(255),

	ADD COLUMN email                  VARCHAR(255),
	ADD COLUMN telefon                VARCHAR(255),
	ADD COLUMN webseite               VARCHAR(255),
	ADD COLUMN betreuungsadressen     JSONB        NOT NULL DEFAULT '[]',

	ADD COLUMN oeffnungstage          JSONB        NOT NULL DEFAULT '[]',
	ADD COLUMN offenvon               TIME,
	ADD COLUMN offenbis               TIME,
	ADD COLUMN oeffnungsabweichungen  VARCHAR(4000),

	ADD COLUMN alterskategorien       JSONB        NOT NULL DEFAULT '[]',
	ADD COLUMN subventionierteplaetze BOOLEAN      NOT NULL DEFAULT FALSE,
	ADD COLUMN anzahlplaetze          NUMERIC(19, 2),
	ADD COLUMN anzahlplaetzefirmen    NUMERIC(19, 2),
	ADD COLUMN timestampmutiert       TIMESTAMP    NOT NULL DEFAULT now();

ALTER TABLE institution
	ALTER COLUMN betreuungsart DROP DEFAULT,
	ALTER COLUMN betreuungsadressen DROP DEFAULT,
	ALTER COLUMN oeffnungstage DROP DEFAULT,
	ALTER COLUMN alterskategorien DROP DEFAULT,
	ALTER COLUMN subventionierteplaetze DROP DEFAULT,
	ALTER COLUMN timestampmutiert DROP DEFAULT;
