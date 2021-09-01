CREATE TABLE anmeldung (
	id                        BIGSERIAL    NOT NULL
		CONSTRAINT anmeldung_pkey PRIMARY KEY,
	institutionid             VARCHAR(255) NOT NULL,
	refnr                     VARCHAR(255) NOT NULL,
	periodevon                DATE         NOT NULL,
	periodebis                DATE         NOT NULL,
	status                    VARCHAR(255) NOT NULL,
	version                   INTEGER      NOT NULL,
	abholung                  VARCHAR(255),
	abweichungzweitessemester BOOLEAN      NOT NULL,
	anmeldungzurueckgezogen   BOOLEAN      NOT NULL,
	bemerkung                 VARCHAR(255),
	eintrittsdatum            DATE         NOT NULL,
	eventtimestamp            TIMESTAMP,
	freigegebenam             DATE         NOT NULL,
	gesuchsteller             JSONB        NOT NULL,
	kind                      JSONB        NOT NULL,
	planklasse                VARCHAR(255),
	module                    JSONB        NOT NULL
);

CREATE TABLE clientanmeldung (
	id                   BIGSERIAL    NOT NULL
		CONSTRAINT clientanmeldung_pkey PRIMARY KEY,
	active               BOOLEAN      NOT NULL,
	client_clientname    VARCHAR(255) NOT NULL,
	client_institutionid VARCHAR(255) NOT NULL,
	anmeldung_id         BIGINT       NOT NULL
		CONSTRAINT client_anmeldung_anmeldung_fk REFERENCES anmeldung,
	CONSTRAINT client_fk
		FOREIGN KEY (client_clientname, client_institutionid) REFERENCES client
);

CREATE INDEX clientanmeldung_idx1 ON clientanmeldung(client_clientname, active, id);

CREATE INDEX anmeldung_idx1 ON anmeldung(institutionid);
CREATE INDEX anmeldung_idx2 ON anmeldung(refnr);

-- region new anmeldung trigger
CREATE FUNCTION anmeldung_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	INSERT INTO clientanmeldung (id, active, client_clientname, client_institutionid, anmeldung_id)
		(SELECT nextval('clientanmeldung_id_seq'), c.active, c.clientname, c.institutionid, new.id
		 FROM client c
		 WHERE c.institutionid = new.institutionid);
	RETURN new;
END;
$$;

CREATE TRIGGER clientanmeldung_insert
	AFTER INSERT
	ON anmeldung
	FOR EACH ROW
EXECUTE PROCEDURE anmeldung_insert();
-- endregion

-- region new client trigger

CREATE FUNCTION clientanmeldung_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	INSERT INTO clientanmeldung (id, active, client_clientname, client_institutionid, anmeldung_id)
		(SELECT nextval('clientanmeldung_id_seq'), new.active, new.clientname, new.institutionid, am.id
		 FROM anmeldung am
		 WHERE am.institutionid = new.institutionid
		 ORDER BY am.id);
	RETURN new;
END;
$$;

CREATE TRIGGER clientanmeldung_insert
	AFTER INSERT
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE clientanmeldung_insert();
-- endregion

-- region toggle active flag

CREATE FUNCTION clientanmeldung_active_toggle() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	UPDATE clientanmeldung
	SET active = new.active
	WHERE client_clientname = new.clientname AND client_institutionid = new.institutionid;
	RETURN new;
END;
$$;

CREATE TRIGGER clientanmeldung_active_toggle
	AFTER UPDATE
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE clientanmeldung_active_toggle();
-- endregion
