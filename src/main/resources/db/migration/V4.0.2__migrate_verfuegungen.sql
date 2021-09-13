ALTER TABLE verfuegung
	RENAME COLUMN von TO periodevon;

ALTER TABLE verfuegung
	RENAME COLUMN bis TO periodebis;

CREATE INDEX consumedmessage_idx1 ON consumedmessage(eventid, timeofreceiving);
