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

INSERT INTO client (clientname, grantedsince, institutionid, active, gueltigab, gueltigbis)
VALUES ('kitAdmin', now(), '1', TRUE, NULL, NULL),
	   ('kitAdmin', now() - INTERVAL '3 days', '2', TRUE,  '2021-01-01'::DATE, NULL),
	   ('kitAdmin', now() - INTERVAL '4 days', '3', FALSE, NULL, NULL),
	   ('KiD', now() - INTERVAL '5 days', '2', FALSE, NULL, NULL),
	   ('CSE', now() - INTERVAL '3 days', '1', TRUE, NULL, NULL);

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
		'AKTIV', NULL, NULL),
	   ('3', 'DV Teens', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '[]', '07:00', '19:00', NULL, '[]', FALSE, NULL, NULL, now(),
		'DELETED', NULL, NULL),
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
	       -- adding a default test case from kiBon test: period 2020/2021, 80% pensum until 2021-01-31, then 0%
		   ('KITA', '2021-07-31'::DATE,
		    '{"email": "test@mailbucket.dvbern.ch", "vorname": "Dagmar", "nachname": "Wälti"}'::JSONB,
		    '[]'::JSONB,
		    '2',
		    0,
		    'Gemeinde',
		    '{"vorname": "Simon", "nachname": "Wälti", "geburtsdatum": "2014-04-13"}'::JSONB,
		    '20.000357.071.1.1',
			now() - INTERVAL '5 days',
		    0,
		    '2020-08-01'::DATE,
		    '[{"bis": "2020-08-31", "von": "2020-08-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2020-09-30", "von": "2020-09-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2020-10-31", "von": "2020-10-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2020-11-30", "von": "2020-11-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2020-12-31", "von": "2020-12-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-01-31", "von": "2021-01-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-02-28", "von": "2021-02-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-03-31", "von": "2021-03-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-04-30", "von": "2021-04-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-05-31", "von": "2021-05-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-06-30", "von": "2021-06-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-07-31", "von": "2021-07-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}]'::JSONB),
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
