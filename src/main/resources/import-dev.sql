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
-- endregion

INSERT INTO client (clientname, grantedsince, institutionid, active)
VALUES ('kitAdmin', now(), '1', TRUE),
	   ('kitAdmin', now() - INTERVAL '3 days', '2', TRUE),
	   ('KiD', now() - INTERVAL '5 days', '2', FALSE),
	   ('CSE', now() - INTERVAL '3 days', '1', TRUE);

INSERT INTO institution (id, adresszusatz, hausnummer, land, ort, plz, strasse, name, traegerschaft)
VALUES ('1', NULL, '21', 'CH', 'Bern', '3006', 'Nussbaumstrasse', 'DV Kids', 'DV Bern AG'),
	   ('2', NULL, '21', 'CH', 'Bern', '3022', 'Nussbaumstrasse', 'DV Juniors', 'DV Bern AG');

INSERT INTO verfuegung (betreuungsart, bis, gesuchsteller, ignoriertezeitabschnitte, institutionid,
						kind, refnr, verfuegtam, version, von, zeitabschnitte)
SELECT t.*
FROM generate_series(1, 100) i
	 CROSS JOIN LATERAL (
	VALUES ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', '{}'::JSONB, '1.1.1.1',
			now(), 0, '2019-08-01'::DATE, '[]'::JSONB),
		   ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', '{}'::JSONB, '1.1.1.1',
			now() - INTERVAL '5 days', 0, '2019-08-01'::DATE, '[]'::JSONB),
		   ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '2', '{}'::JSONB, '1.1.1.1',
			now() - INTERVAL '5 days', 0, '2019-08-01'::DATE, '[]'::JSONB),
		   ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', '{}'::JSONB, '1.1.1.1',
			now() - INTERVAL '7 days', 0, '2019-08-01'::DATE, '[]'::JSONB)
	) t;
