CREATE TABLE betreuungstornierunganfrage (
	id BIGSERIAL NOT NULL CONSTRAINT betreuungstornierunanfrage_pkey PRIMARY KEY,
	refnr VARCHAR(255) NOT NULL,
	institutionid VARCHAR(255) NOT NULL,
	eventtimestamp TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX betreuungstornierunganfrage_idx1
	ON betreuungstornierunganfrage(institutionid);