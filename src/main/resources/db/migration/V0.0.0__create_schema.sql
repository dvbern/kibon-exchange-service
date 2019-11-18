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
	refnr                    VARCHAR(255) NOT NULL,
	institutionid            VARCHAR(255) NOT NULL,
	von                      DATE         NOT NULL,
	bis                      DATE         NOT NULL,
	version                  INTEGER      NOT NULL
		CONSTRAINT verfuegung_version_check
			CHECK (version >= 0),
	verfuegtam               TIMESTAMP    NOT NULL,
	betreuungsart            VARCHAR(255) NOT NULL,
	gemeindebfsnr            BIGINT       NOT NULL,
	gemeindename             VARCHAR(255) NOT NULL,
	kind                     JSONB        NOT NULL,
	gesuchsteller            JSONB        NOT NULL,
	zeitabschnitte           JSONB        NOT NULL,
	ignoriertezeitabschnitte JSONB        NOT NULL
);

CREATE TABLE clientverfuegung (
	id                   BIGSERIAL    NOT NULL
		CONSTRAINT clientverfuegung_pkey
			PRIMARY KEY,
	active               BOOLEAN      NOT NULL,
	since                TIMESTAMP    NOT NULL,
	client_clientname    VARCHAR(255) NOT NULL,
	client_institutionid VARCHAR(255) NOT NULL,
	verfuegung_id        BIGINT       NOT NULL
		CONSTRAINT verfuegung_fk
			REFERENCES verfuegung,
	CONSTRAINT client_fk
		FOREIGN KEY (client_clientname, client_institutionid) REFERENCES client
);

CREATE INDEX clientverfuegung_idx1
	ON clientverfuegung(client_clientname, active, since, id);

CREATE INDEX verfuegung_idx1
	ON verfuegung(institutionid, verfuegtam);

CREATE TABLE institution (
	id            VARCHAR(255) NOT NULL
		CONSTRAINT institution_pkey
			PRIMARY KEY,
	name          VARCHAR(255),
	traegerschaft VARCHAR(255),
	strasse       VARCHAR(255) NOT NULL,
	hausnummer    VARCHAR(255),
	adresszusatz  VARCHAR(255),
	plz           VARCHAR(255) NOT NULL,
	ort           VARCHAR(255) NOT NULL,
	land          VARCHAR(255)
);
