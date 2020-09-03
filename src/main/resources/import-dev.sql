-- region account for FlyWay migrations

/*
 since hibernate recreates the schema (drop-and-create) after the FlyWay migrations are executed,
 every modification of the entity schema in FlyWay must be reapplied.
 The procedures are stored in the database schema, but triggers are table specific and must be reacreated.
*/

CREATE TRIGGER verfuegung_insert
	AFTER INSERT
	ON verfuegung
	FOR EACH ROW
EXECUTE PROCEDURE verfuegung_insert();

CREATE TRIGGER client_insert
	AFTER INSERT
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE client_insert();

CREATE TRIGGER client_active_toggle
	AFTER UPDATE
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE client_active_toggle();

CREATE TRIGGER betreuunganfrage_insert
	AFTER INSERT
	ON betreuunganfrage
	FOR EACH ROW
EXECUTE PROCEDURE betreuunganfrage_insert();

CREATE TRIGGER clientbetreuunganfrage_insert
	AFTER INSERT
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE clientbetreuunganfrage_insert();

CREATE TRIGGER clientbetreuunganfrage_active_toggle
	AFTER UPDATE
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE clientbetreuunganfrage_active_toggle();
-- endregion

INSERT INTO client (clientname, grantedsince, institutionid, active)
VALUES ('kitAdmin', now(), '1', TRUE),
	   ('kitAdmin', now() - INTERVAL '3 days', '2', TRUE),
	   ('kitAdmin', now() - INTERVAL '4 days', '3', FALSE),
	   ('KiD', now() - INTERVAL '5 days', '2', FALSE),
	   ('CSE', now() - INTERVAL '3 days', '1', TRUE);

INSERT INTO institution(id, name, traegerschaft, anschrift, strasse, hausnummer, adresszusatz, plz, ort, land,
						betreuungsart, bfsnummer, gemeinde_name, email, telefon, webseite, betreuungsadressen,
						oeffnungstage, offenvon, offenbis, oeffnungsabweichungen, alterskategorien,
						subventionierteplaetze, anzahlplaetze, anzahlplaetzefirmen, timestampmutiert)
VALUES ('1', 'DV Kids', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3006', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL, now()),
	   ('2', 'DV Juniors', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL, now()),
	   ('3', 'DV Teens', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL, now()),
	   ('4', 'DV Tweens', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'TAGESSCHULE', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL, now());

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
							 abgelehntvongesuchsteller)
SELECT t.*
FROM generate_series(1, 10) i
	 CROSS JOIN LATERAL (
	VALUES ('1.1.1.1', '1', '2019-08-01'::DATE, '2020-07-31'::DATE, 'KITA', '{}'::JSONB, '{}'::JSONB, FALSE),
		   ('1.1.1.2', '2', '2019-08-01'::DATE, '2020-07-31'::DATE, 'KITA', '{}'::JSONB, '{}'::JSONB, FALSE),
		   ('1.1.1.3', '3', '2019-08-01'::DATE, '2020-07-31'::DATE, 'KITA', '{}'::JSONB, '{}'::JSONB, FALSE)
	) t;
