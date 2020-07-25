CREATE TABLE betreuunganfrage (
	id                       BIGSERIAL    NOT NULL
		CONSTRAINT betreuunganfrage_pkey
			PRIMARY KEY,
	refnr                    VARCHAR(255) NOT NULL,
	institutionid            VARCHAR(255) NOT NULL,
	periodevon                      DATE         NOT NULL,
	periodebis                      DATE         NOT NULL,
	verfuegtam               TIMESTAMP    NOT NULL,
	betreuungsart            VARCHAR(255) NOT NULL,
	kind                     JSONB        NOT NULL,
	gesuchsteller            JSONB        NOT NULL,
	abgelehntVonGesuchsteller BOOLEAN        NOT NULL
);

CREATE TABLE clientbetreuunganfrage (
	id                   BIGSERIAL    NOT NULL
		CONSTRAINT clientbetreuunganfrage_pkey
			PRIMARY KEY,
	active               BOOLEAN      NOT NULL,
	since                TIMESTAMP    NOT NULL,
	client_clientname    VARCHAR(255) NOT NULL,
	client_institutionid VARCHAR(255) NOT NULL,
	betreuunganfrage_id        BIGINT       NOT NULL
		CONSTRAINT betreuunganfrage_fk
			REFERENCES betreuunganfrage,
	CONSTRAINT client_fk
		FOREIGN KEY (client_clientname, client_institutionid) REFERENCES client
);

CREATE INDEX clientbetreuunganfrage_idx1
	ON clientbetreuunganfrage(client_clientname, active, since, id);

CREATE INDEX betreuunganfrage_idx1
	ON betreuunganfrage(institutionid);