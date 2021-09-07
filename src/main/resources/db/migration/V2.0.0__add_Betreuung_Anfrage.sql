CREATE TABLE betreuunganfrage (
	id                        BIGSERIAL    NOT NULL
		CONSTRAINT betreuunganfrage_pkey PRIMARY KEY,
	refnr                     VARCHAR(255) NOT NULL,
	institutionid             VARCHAR(255) NOT NULL,
	periodevon                DATE         NOT NULL,
	periodebis                DATE         NOT NULL,
	betreuungsart             VARCHAR(255) NOT NULL,
	kind                      JSONB        NOT NULL,
	gesuchsteller             JSONB        NOT NULL,
	abgelehntVonGesuchsteller BOOLEAN      NOT NULL
);

CREATE TABLE clientbetreuunganfrage (
	id                   BIGSERIAL    NOT NULL
		CONSTRAINT clientbetreuunganfrage_pkey PRIMARY KEY,
	active               BOOLEAN      NOT NULL,
	since                TIMESTAMP    NOT NULL,
	client_clientname    VARCHAR(255) NOT NULL,
	client_institutionid VARCHAR(255) NOT NULL,
	betreuunganfrage_id  BIGINT       NOT NULL
		CONSTRAINT betreuunganfrage_fk REFERENCES betreuunganfrage,
	CONSTRAINT client_fk FOREIGN KEY (client_clientname, client_institutionid) REFERENCES client
);

CREATE INDEX clientbetreuunganfrage_idx1
	ON clientbetreuunganfrage(client_clientname, active, since, id);

CREATE INDEX betreuunganfrage_idx1
	ON betreuunganfrage(institutionid);

CREATE FUNCTION betreuunganfrage_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	INSERT INTO clientbetreuunganfrage (id, active, client_clientname, client_institutionid, betreuunganfrage_id, since)
		(SELECT nextval('clientbetreuunganfrage_id_seq'), c.active, c.clientname, c.institutionid, new.id,
				c.grantedsince
		 FROM client c
		 WHERE c.institutionid = new.institutionid);
	RETURN new;
END;
$$;

CREATE TRIGGER betreuunganfrage_insert
	AFTER INSERT
	ON betreuunganfrage
	FOR EACH ROW
EXECUTE PROCEDURE betreuunganfrage_insert();
-- endregion

-- region new client trigger

CREATE FUNCTION clientbetreuunganfrage_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	INSERT INTO clientbetreuunganfrage (id, active, client_clientname, client_institutionid, betreuunganfrage_id, since)
		(SELECT nextval('clientbetreuunganfrage_id_seq'), new.active, new.clientname, new.institutionid, ba.id,
				new.grantedsince
		 FROM betreuunganfrage ba
		 WHERE ba.institutionid = new.institutionid
		 ORDER BY new.grantedsince, ba.id);
	RETURN new;
END;
$$;

CREATE TRIGGER clientbetreuunganfrage_insert
	AFTER INSERT
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE clientbetreuunganfrage_insert();
-- endregion

-- region toggle active flag

CREATE FUNCTION clientbetreuunganfrage_active_toggle() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	UPDATE clientbetreuunganfrage
	SET active = new.active
	WHERE client_clientname = new.clientname AND client_institutionid = new.institutionid;
	RETURN new;
END;
$$;

CREATE TRIGGER clientbetreuunganfrage_active_toggle
	AFTER UPDATE
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE clientbetreuunganfrage_active_toggle();
-- endregion
