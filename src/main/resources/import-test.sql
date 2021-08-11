-- It doesn't work to use FlyWay migrations and the import SQL file together. The order of execution is inconsistent.
-- Therefore, for integration tests, FlyWay is disabled. All required migrations must be added here.
-- The schema is intentionally auto-generated by Hibernate, to catch deviations.

-- Unfortunately, Hibernates MultipleLinesSqlCommandExtractor does not really support $$ quoating, thus string quotes
-- are used here. https://stackoverflow.com/a/50380323

-- region Verfuegung
-- region new verfuegung trigger
CREATE FUNCTION verfuegung_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
'
	BEGIN
		INSERT INTO clientverfuegung (id, active, client_clientname, client_institutionid, verfuegung_id, since)
			(SELECT nextval(''clientverfuegung_id_seq''), c.active, c.clientname, c.institutionid, new.id,
					greatest(c.grantedsince, new.verfuegtam)
			 FROM client c
			 WHERE c.institutionid = new.institutionid);
		RETURN new;
	END;
';


CREATE TRIGGER verfuegung_insert
	AFTER INSERT
	ON verfuegung
	FOR EACH ROW
EXECUTE PROCEDURE verfuegung_insert();
-- endregion

-- region new client trigger

CREATE FUNCTION client_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
'
	BEGIN
		INSERT INTO clientverfuegung (id, active, client_clientname, client_institutionid, verfuegung_id, since)
			(SELECT nextval(''clientverfuegung_id_seq''), new.active, new.clientname, new.institutionid, v.id,
					greatest(new.grantedsince, v.verfuegtam)
			 FROM verfuegung v
			 WHERE v.institutionid = new.institutionid
			 ORDER BY greatest(new.grantedsince, v.verfuegtam), v.id);
		RETURN new;
	END;
';

CREATE TRIGGER client_insert
	AFTER INSERT
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE client_insert();
-- endregion

-- region toggle active flag

CREATE FUNCTION client_active_toggle() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
'
	BEGIN
		UPDATE clientverfuegung
		SET active = new.active
		WHERE client_clientname = new.clientname AND client_institutionid = new.institutionid;
		RETURN new;
	END;
';

CREATE TRIGGER client_active_toggle
	AFTER UPDATE
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE client_active_toggle();
-- endregion
-- endregion

-- region Platzbestaetigung

CREATE FUNCTION betreuunganfrage_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
'
	BEGIN
		INSERT INTO clientbetreuunganfrage (id, active, client_clientname, client_institutionid, betreuunganfrage_id)
			(SELECT nextval(''clientbetreuunganfrage_id_seq''), c.active, c.clientname, c.institutionid, new.id
			 FROM client c
			 WHERE c.institutionid = new.institutionid);
		RETURN new;
	END;
';

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
'
	BEGIN
		INSERT INTO clientbetreuunganfrage (id, active, client_clientname, client_institutionid, betreuunganfrage_id)
			(SELECT nextval(''clientbetreuunganfrage_id_seq''), new.active, new.clientname, new.institutionid, ba.id
			 FROM betreuunganfrage ba
			 WHERE ba.institutionid = new.institutionid
			 ORDER BY ba.id);
		RETURN new;
	END;
';

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
'
	BEGIN
		UPDATE clientbetreuunganfrage
		SET active = new.active
		WHERE client_clientname = new.clientname AND client_institutionid = new.institutionid;
		RETURN new;
	END;
';

CREATE TRIGGER clientbetreuunganfrage_active_toggle
	AFTER UPDATE
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE clientbetreuunganfrage_active_toggle();
-- endregion
-- endregion

INSERT INTO client (clientname, grantedsince, institutionid, active)
VALUES ('kitAdmin', now(), '1', TRUE),
	   ('kitAdmin', now() - INTERVAL '3 days', '2', TRUE),
	   ('kitAdmin', now() - INTERVAL '4 days', '3', FALSE),
	   ('kitAdmin', now(), '4', TRUE),
	   ('KiD', now() - INTERVAL '5 days', '2', FALSE),
	   ('CSE', now() - INTERVAL '3 days', '1', TRUE);

INSERT INTO institution(id, name, traegerschaft, anschrift, strasse, hausnummer, adresszusatz, plz, ort, land,
						betreuungsart, bfsnummer, gemeinde_name, email, telefon, webseite, betreuungsadressen,
						oeffnungstage, offenvon, offenbis, oeffnungsabweichungen, alterskategorien,
						subventionierteplaetze, anzahlplaetze, anzahlplaetzefirmen, timestampmutiert,
						status, betreuungsgutscheineab, betreuungsgutscheinebis)
VALUES ('1', 'DV Kids', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3006', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL, now(),
		'AKTIV', NULL, NULL),
	   ('2', 'DV Juniors', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL, now(),
		'DELETED', NULL, NULL),
	   ('3', 'DV Teens', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL, now(),
		'AKTIV', NULL, NULL),
	   ('4', 'DV Tweens', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'TAGESSCHULE', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL,
		now(), 'AKTIV', NULL, NULL);

INSERT INTO verfuegung (betreuungsart, bis, gesuchsteller, ignoriertezeitabschnitte, institutionid, gemeindebfsnr,
						gemeindename, kind, refnr, verfuegtam, version, von, zeitabschnitte)
SELECT t.*
FROM generate_series(1, 100) i
	 CROSS JOIN LATERAL (
	VALUES ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', 0, 'Gemeinde', '{}'::JSONB, '1.1.1.1',
			now(), 0, '2019-08-01'::DATE, '[]'::JSONB),
		   ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', 0, 'Gemeinde', '{}'::JSONB, '1.1.1.1',
			now() - INTERVAL '5 days', 0, '2019-08-01'::DATE, '[]'::JSONB),
		   ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '2', 0, 'Gemeinde', '{}'::JSONB, '1.1.1.1',
			now() - INTERVAL '5 days', 0, '2019-08-01'::DATE, '[]'::JSONB),
		   ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', 0, 'Gemeinde', '{}'::JSONB, '1.1.1.1',
			now() - INTERVAL '7 days', 0, '2019-08-01'::DATE, '[]'::JSONB)
	) t;

INSERT INTO betreuunganfrage(refnr, institutionid, periodevon, periodebis, betreuungsart, kind, gesuchsteller,
							 abgelehntvongesuchsteller, eventtimestamp)
SELECT t.*
FROM generate_series(1, 10) i
	 CROSS JOIN LATERAL (
	VALUES ('1.1.1.1', '1', '2019-08-01'::DATE, '2020-07-31'::DATE, 'KITA', '{}'::JSONB, '{}'::JSONB, FALSE, now()),
		   ('1.1.1.2', '2', '2019-08-01'::DATE, '2020-07-31'::DATE, 'KITA', '{}'::JSONB, '{}'::JSONB, FALSE, now()),
		   ('1.1.1.3', '3', '2019-08-01'::DATE, '2020-07-31'::DATE, 'KITA', '{}'::JSONB, '{}'::JSONB, FALSE, now())
	) t;

-- region anmeldung trigger

CREATE FUNCTION anmeldung_insert() RETURNS TRIGGER SECURITY DEFINER
	LANGUAGE plpgsql
AS
'
BEGIN
INSERT INTO clientanmeldung (id, active, client_clientname, client_institutionid, anmeldung_id)
	(SELECT nextval(''clientanmeldung_id_seq''), c.active, c.clientname, c.institutionid, new.id
	 FROM client c
	 WHERE c.institutionid = new.institutionid);
RETURN new;
END;
';

CREATE TRIGGER clientanmeldung_insert
	AFTER INSERT
	ON anmeldung
	FOR EACH ROW
	EXECUTE PROCEDURE anmeldung_insert();

CREATE FUNCTION clientanmeldung_insert() RETURNS TRIGGER SECURITY DEFINER
	LANGUAGE plpgsql
AS
'
BEGIN
INSERT INTO clientanmeldung (id, active, client_clientname, client_institutionid, anmeldung_id)
	(SELECT nextval(''clientanmeldung_id_seq''), new.active, new.clientname, new.institutionid, am.id
	 FROM anmeldung am
	 WHERE am.institutionid = new.institutionid
	 ORDER BY am.id);
RETURN new;
END;
';

CREATE TRIGGER clientanmeldung_insert
	AFTER INSERT
	ON client
	FOR EACH ROW
	EXECUTE PROCEDURE clientanmeldung_insert();

CREATE FUNCTION clientanmeldung_active_toggle() RETURNS TRIGGER SECURITY DEFINER
	LANGUAGE plpgsql
AS
'
BEGIN
UPDATE clientanmeldung
SET active = new.active
WHERE client_clientname = new.clientname AND client_institutionid = new.institutionid;
RETURN new;
END;
';

CREATE TRIGGER clientanmeldung_active_toggle
	AFTER UPDATE
	ON client
	FOR EACH ROW
	EXECUTE PROCEDURE clientanmeldung_active_toggle();

-- endregion

INSERT INTO gesuchsperiode (id, gueltigab, gueltigbis)
VALUES ('1001', '2020-08-01'::DATE, '2021-07-31'::DATE);

INSERT INTO anmeldung (id, kind, gesuchsteller, freigegebenam, status, anmeldungzurueckgezogen, refnr, eintrittsdatum,
					   planklasse, abholung, abweichungzweitessemester, bemerkung, anmeldungmodule, gesuchsperiode_id, institutionid,
					   eventtimestamp, version)
VALUES ('1002', '{}'::JSONB, '{}'::JSONB, '2021-07-26'::DATE, 'SCHULAMT_ANMELDUNG_ERFASST', FALSE, '20.000101.001.1.1',
		'2020-08-01'::DATE, '3a', 'ABHOLUNG', FALSE, 'test Bemerkung','[]'::JSONB, '1001', '4', now(), 0);