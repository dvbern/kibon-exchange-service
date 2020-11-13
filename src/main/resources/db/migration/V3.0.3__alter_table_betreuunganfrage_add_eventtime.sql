ALTER TABLE betreuunganfrage
	ADD COLUMN eventtimestamp TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE betreuunganfrage
	ALTER COLUMN eventtimestamp DROP DEFAULT;
