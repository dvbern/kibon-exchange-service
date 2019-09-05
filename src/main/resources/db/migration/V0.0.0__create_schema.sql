CREATE TABLE client (
	clientname    VARCHAR(255) NOT NULL,
	institutionid VARCHAR(255) NOT NULL,
	active        BOOLEAN      NOT NULL,
	grantedsince  TIMESTAMP    NOT NULL,
	CONSTRAINT client_pkey
		PRIMARY KEY (clientname, institutionid)
);

CREATE INDEX client_idx1
	ON client(clientname, institutionid, grantedsince);

CREATE TABLE consumedmessage (
	eventid         UUID      NOT NULL
		CONSTRAINT consumedmessage_pkey
			PRIMARY KEY,
	timeofreceiving TIMESTAMP NOT NULL
);

CREATE TABLE verfuegung (
	id                       BIGSERIAL    NOT NULL
		CONSTRAINT verfuegung_pkey
			PRIMARY KEY,
	betreuungsart            VARCHAR(255) NOT NULL,
	bis                      DATE         NOT NULL,
	gesuchsteller            JSONB        NOT NULL,
	ignoriertezeitabschnitte JSONB        NOT NULL,
	institutionid            VARCHAR(255) NOT NULL,
	kind                     JSONB        NOT NULL,
	refnr                    VARCHAR(255) NOT NULL,
	verfuegtam               TIMESTAMP    NOT NULL,
	version                  INTEGER      NOT NULL
		CONSTRAINT verfuegung_version_check
			CHECK (version >= 0),
	von                      DATE         NOT NULL,
	zeitabschnitte           JSONB        NOT NULL
);

CREATE TABLE clientverfuegung (
	id            BIGSERIAL    NOT NULL
		CONSTRAINT clientverfuegung_pkey
			PRIMARY KEY,
	clientname    VARCHAR(255) NOT NULL,
	institutionid VARCHAR(255) NOT NULL,
	since         TIMESTAMP    NOT NULL,
	verfuegung_id BIGINT       NOT NULL
		CONSTRAINT verfuegung_fk
			REFERENCES verfuegung
);

CREATE INDEX clientverfuegung_idx1
	ON clientverfuegung(clientname, since, id);

CREATE INDEX verfuegung_idx1
	ON verfuegung(institutionid, verfuegtam);

CREATE TABLE institution (
	id            VARCHAR(255) NOT NULL
		CONSTRAINT institution_pkey
			PRIMARY KEY,
	adresszusatz  VARCHAR(255),
	hausnummer    VARCHAR(255),
	land          VARCHAR(255),
	ort           VARCHAR(255) NOT NULL,
	plz           VARCHAR(255) NOT NULL,
	strasse       VARCHAR(255) NOT NULL,
	name          VARCHAR(255),
	traegerschaft VARCHAR(255)
);
