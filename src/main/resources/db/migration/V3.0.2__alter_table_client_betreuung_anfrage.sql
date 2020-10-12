DROP INDEX clientbetreuunganfrage_idx1;

ALTER TABLE clientbetreuunganfrage drop column since;

CREATE INDEX clientbetreuunganfrage_idx1
	ON clientbetreuunganfrage(client_clientname, active, id);

CREATE OR REPLACE FUNCTION betreuunganfrage_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	INSERT INTO clientbetreuunganfrage (id, active, client_clientname, client_institutionid, betreuunganfrage_id)
		(SELECT nextval('clientbetreuunganfrage_id_seq'), c.active, c.clientname, c.institutionid, new.id
		 FROM client c
		 WHERE c.institutionid = new.institutionid);
	RETURN new;
END;
$$;

CREATE OR REPLACE FUNCTION clientbetreuunganfrage_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	INSERT INTO clientbetreuunganfrage (id, active, client_clientname, client_institutionid, betreuunganfrage_id)
		(SELECT nextval('clientbetreuunganfrage_id_seq'), new.active, new.clientname, new.institutionid, ba.id
		 FROM betreuunganfrage ba
		 WHERE ba.institutionid = new.institutionid
		 ORDER BY ba.id);
	RETURN new;
END;
$$;