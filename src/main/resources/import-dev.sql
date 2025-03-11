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

CREATE TRIGGER clientanmeldung_insert
	AFTER INSERT
	ON anmeldung
	FOR EACH ROW
EXECUTE PROCEDURE anmeldung_insert();

CREATE TRIGGER clientanmeldung_insert
	AFTER INSERT
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE clientanmeldung_insert();
-- endregion

INSERT INTO client (clientname, grantedsince, institutionid, active, gueltigab, gueltigbis)
VALUES ('kitAdmin', now(), '1', TRUE, NULL, NULL),
	   ('kitAdmin', now() - INTERVAL '3 days', '2', TRUE, '2021-01-01'::DATE, NULL),
	   ('kitAdmin', now() - INTERVAL '4 days', '3', FALSE, NULL, NULL),
	   ('kitAdmin', now(), '5', TRUE, NULL, NULL),
	   ('KiD', now() - INTERVAL '5 days', '2', FALSE, NULL, NULL),
	   ('CSE', now() - INTERVAL '3 days', '1', TRUE, NULL, NULL);

INSERT INTO institution(id, name, traegerschaft, anschrift, strasse, hausnummer, adresszusatz, plz, ort, land,
						betreuungsart, bfsnummer, gemeinde_name, email, telefon, webseite,
						oeffnungstage, offenvon, offenbis, oeffnungsabweichungen, alterskategorien,
						anzahlplaetze, anzahlplaetzefirmen, timestampmutiert,
						status, betreuungsgutscheineab, betreuungsgutscheinebis, mandant)
VALUES ('1', 'DV Kids', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3006', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '07:00', '19:00', NULL, '[]', NULL, NULL, now(),
		'AKTIV', NULL, NULL, 'BERN'),
	   ('2', 'DV Juniors', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '07:00', '19:00', NULL, '[]', NULL, NULL, now(),
		'AKTIV', NULL, NULL, 'BERN'),
	   ('3', 'DV Teens', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'KITA', NULL, NULL, NULL, NULL, NULL, '[]', '07:00', '19:00', NULL, '[]', NULL, NULL, now(),
		'DELETED', NULL, NULL, 'BERN'),
	   ('4', 'DV Tweens', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
		'TAGESSCHULE', NULL, NULL, NULL, NULL, NULL, '[]', '07:00', '19:00', NULL, '[]', NULL, NULL,
		now(), 'AKTIV', NULL, NULL, 'BERN'),
		('5', 'TS Test', 'DV Bern AG', NULL, 'Nussbaumstrasse', '21', NULL, '3022', 'Bern', 'CH',
 		'TAGESSCHULE', NULL, NULL, NULL, NULL, NULL, '[]', '07:00', '19:00', NULL, '[]', NULL, NULL,
 		now(), 'AKTIV', NULL, NULL, 'BERN');

INSERT INTO verfuegung (betreuungsart, periodebis, gesuchsteller, ignoriertezeitabschnitte, institutionid, mandant,
						gemeindebfsnr, gemeindename, auszahlunganeltern, kind, refnr, verfuegtam, version, periodevon, zeitabschnitte)
SELECT t.*
FROM generate_series(1, 100) i
	 CROSS JOIN LATERAL (
	VALUES ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', 'BERN', 0, 'Gemeinde', FALSE, '{}'::JSONB, '1.1.1.1',
			now(), 0, '2019-08-01'::DATE, '[]'::JSONB),
		   ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', 'BERN', 0, 'Gemeinde', FALSE, '{}'::JSONB, '1.1.1.1',
			now() - INTERVAL '5 days', 0, '2019-08-01'::DATE, '[]'::JSONB),
	       -- adding a default test case from kiBon test: period 2020/2021, 80% pensum until 2021-01-31, then 0%
		   ('KITA', '2021-07-31'::DATE,
		    '{"email": "test@mailbucket.dvbern.ch", "vorname": "Dagmar", "nachname": "Wälti"}'::JSONB,
		    '[]'::JSONB,
		    '2',
		    'BERN',
		    0,
		    'Gemeinde',
		    FALSE,
		    '{"vorname": "Simon", "nachname": "Wälti", "geburtsdatum": "2014-04-13"}'::JSONB,
		    '20.000357.071.1.1',
			now() - INTERVAL '5 days',
		    0,
		    '2020-08-01'::DATE,
		    '[{"bis": "2020-08-31", "von": "2020-08-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2020-09-30", "von": "2020-09-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2020-10-31", "von": "2020-10-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2020-11-30", "von": "2020-11-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2020-12-31", "von": "2020-12-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-01-31", "von": "2021-01-01", "regelwerk": "ASIV", "vollkosten": 2000.0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 1451.3, "verguenstigtPct": 80.0, "betreuungsgutschein": 1451.3, "effektiveBetreuungPct": 80.0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 16, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-02-28", "von": "2021-02-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-03-31", "von": "2021-03-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-04-30", "von": "2021-04-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-05-31", "von": "2021-05-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-06-30", "von": "2021-06-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}, {"bis": "2021-07-31", "von": "2021-07-01", "regelwerk": "ASIV", "vollkosten": 0, "anspruchPct": 100, "zeiteinheit": "DAYS", "verfuegungNr": 0, "verguenstigung": 0, "verguenstigtPct": 0, "betreuungsgutschein": 0, "effektiveBetreuungPct": 0, "minimalerElternbeitrag": 0, "verfuegteAnzahlZeiteinheiten": 0, "anspruchsberechtigteAnzahlZeiteinheiten": 20.0}]'::JSONB),
		   ('KITA', '2020-07-31'::DATE, '{}'::JSONB, '[]'::JSONB, '1', 'BERN', 0, 'Gemeinde', FALSE, '{}'::JSONB, '1.1.1.1',
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


INSERT INTO anmeldung (kind, gesuchsteller, freigegebenam, status, anmeldungzurueckgezogen, refnr, eintrittsdatum,
					   planklasse, abholung, abweichungzweitessemester, bemerkung, module, periodevon,
					   periodebis, institutionid,
					   eventtimestamp, version)
VALUES ('{
	"vorname": "Simon",
	"nachname": "Wälti",
	"geschlecht": "MAENNLICH",
	"geburtsdatum": "2014-04-13"
}'::JSONB,
		'{
			"email": "test@mailbucket.dvbern.ch",
			"adresse": {
				"ort": "Bern",
				"plz": "3000",
				"land": "CH",
				"strasse": "Testweg",
				"hausnummer": "10",
				"adresszusatz": null
			},
			"vorname": "Dagmar",
			"nachname": "Wälti",
			"geschlecht": "WEIBLICH",
			"geburtsdatum": "1980-03-25"
		}'::JSONB,
		'2021-07-26'::DATE,
		'SCHULAMT_ANMELDUNG_ERFASST',
		FALSE,
		'20.000101.001.1.1',
		'2020-08-01'::DATE,
		'3a',
		'ABHOLUNG',
		FALSE,
		'test Bemerkung',
		'[]'::JSONB,
		'2019-08-01'::DATE,
		'2020-07-31'::DATE,
		'5',
		now(),
		0);


-- region Tagesschule test data
INSERT INTO tagesschulemodule (id, periodebis, periodevon, institution_id)
VALUES (1, '2020-07-31', '2019-08-01', '5'),
       (2, '2024-07-31', '2023-08-01', '5'),
       (3, '2021-07-31', '2020-08-01', '5'),
       (4, '2022-07-31', '2021-08-01', '5'),
       (5, '2023-07-31', '2022-08-01', '5');


INSERT INTO modul (id, parent_id, bezeichnungde, bezeichnungfr, erlaubteintervalle, verpflegungskosten,
                   wirdpaedagogischbetreut, wochentage, zeitvon, zeitbis, fremdid)
VALUES ('fb0381a9-af4c-4df3-a52a-f017e53e707e', 2, 'Morgen', 'Matain', '[
    "WOECHENTLICH"
]', 5.00, TRUE, '[
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY"
]', '07:00:00', '12:00:00', NULL),
       ('3b951082-1590-4952-a430-4beced7fc8b6', 2, 'Neu3', 'Neu3', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 1.00, FALSE, '[
           "TUESDAY",
           "WEDNESDAY",
           "THURSDAY"
       ]', '13:10:00', '13:15:00', NULL),
       ('e3081598-7e46-4d1c-aa57-31349884a13a', 2, 'Neu5b', 'Neu5b', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 1.00, FALSE, '[
           "MONDAY"
       ]', '18:00:00', '18:05:00', NULL),
       ('3b74d09d-e0e3-458f-a612-1fb61b56f0ae', 2, 'Neu4', 'Neu4', '[
           "WOECHENTLICH"
       ]', 0.00, TRUE, '[
           "THURSDAY",
           "FRIDAY"
       ]', '13:10:00', '13:11:00', NULL),
       ('a67eb485-84f1-48c5-90d6-dbad4e455915', 2, 'Neu5a', 'Neu5a', '[
           "WOECHENTLICH"
       ]', 0.00, TRUE, '[
           "THURSDAY"
       ]', '15:00:00', '16:00:00', NULL),
       ('0d863591-f8e5-4371-b840-44395df1134c', 2, 'NEU', 'NEU', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 22.00, TRUE, '[
           "THURSDAY"
       ]', '12:00:00', '12:30:00', NULL),
       ('3297db97-acc7-4e9b-b78b-014d11613267', 2, 'Neu2', 'Neu2', '[
           "WOECHENTLICH"
       ]', 10.00, TRUE, '[
           "MONDAY"
       ]', '13:00:00', '13:05:00', NULL),
       ('e3f6e756-3b35-4f2d-a271-55bff241f143', 2, 'Nachmittag', 'Aprés midi', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 3.00, FALSE, '[
           "MONDAY",
           "TUESDAY"
       ]', '14:00:00', '17:00:00', NULL),
       ('208c98c9-f587-46cb-896c-ce87d9af7849', 3, 'Nachmittag', 'Aprés midi', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 3.00, FALSE, '[
           "MONDAY",
           "TUESDAY"
       ]', '14:00:00', '17:00:00', NULL),
       ('bd00f085-1924-42e8-b909-3a64fb7c9f3c', 3, 'Morgen', 'Matain', '[
           "WOECHENTLICH"
       ]', 5.00, TRUE, '[
           "MONDAY",
           "TUESDAY",
           "WEDNESDAY",
           "THURSDAY",
           "FRIDAY"
       ]', '07:00:00', '12:00:00', NULL),
       ('366c7d96-8d26-4ada-890b-9938538021c3', 3, 'Neu3', 'Neu3', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 1.00, FALSE, '[
           "TUESDAY",
           "WEDNESDAY",
           "THURSDAY"
       ]', '13:10:00', '13:15:00', NULL),
       ('46a810a8-c147-4494-ad62-87e8e8e4827b', 3, 'Neu4', 'Neu4', '[
           "WOECHENTLICH"
       ]', 0.00, TRUE, '[
           "THURSDAY",
           "FRIDAY"
       ]', '13:10:00', '13:11:00', NULL),
       ('e1478879-8229-45f0-bedb-b832c01f0095', 3, 'Neu5b', 'Neu5b', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 1.00, FALSE, '[
           "MONDAY"
       ]', '18:00:00', '18:05:00', NULL),
       ('fdf27bd2-249c-418f-b17f-5eb87e2620b8', 3, 'Neu5a', 'Neu5a', '[
           "WOECHENTLICH"
       ]', 0.00, TRUE, '[
           "THURSDAY"
       ]', '15:00:00', '16:00:00', NULL),
       ('c827cfbb-6d80-4b8e-ac7a-36b8b13c410c', 3, 'Neu2', 'Neu2', '[
           "WOECHENTLICH"
       ]', 10.00, TRUE, '[
           "MONDAY"
       ]', '13:00:00', '13:05:00', NULL),
       ('b46257c4-bb1b-4d51-a7fb-ee00d80f4580', 3, 'NEU', 'NEU', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 22.00, TRUE, '[
           "THURSDAY"
       ]', '12:00:00', '12:30:00', NULL),
       ('46a33b3f-cb7b-45f5-b34c-94604b3a8f29', 4, 'Nachmittag', 'Aprés midi', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 3.00, FALSE, '[
           "MONDAY",
           "TUESDAY"
       ]', '14:00:00', '17:00:00', 'Nachmittag'),
       ('dd1a2745-2572-4e02-a85c-e1377addf2de', 4, 'Neu5b', 'Neu5b', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 1.00, FALSE, '[
           "MONDAY"
       ]', '18:00:00', '18:05:00', 'Neu5b'),
       ('4f0938ef-a5f6-44f2-8205-1c5a82a1f9f3', 4, 'Neu3', 'Neu3', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 1.00, FALSE, '[
           "TUESDAY",
           "WEDNESDAY",
           "THURSDAY"
       ]', '13:10:00', '13:15:00', 'Neu3'),
       ('cd11df51-a0bd-476e-bb02-d7b706d694f5', 4, 'Neu5a', 'Neu5a', '[
           "WOECHENTLICH"
       ]', 0.00, TRUE, '[
           "THURSDAY"
       ]', '15:00:00', '16:00:00', 'Neu5a'),
       ('5fe1fd98-8cc2-49db-ad3f-25ac059d3816', 4, 'Morgen', 'Matain', '[
           "WOECHENTLICH"
       ]', 5.00, TRUE, '[
           "MONDAY",
           "TUESDAY",
           "WEDNESDAY",
           "THURSDAY",
           "FRIDAY"
       ]', '07:00:00', '12:00:00', 'Morgen'),
       ('4a0c6d9d-359f-4567-9888-a87c442d2608', 4, 'Neu2', 'Neu2', '[
           "WOECHENTLICH"
       ]', 10.00, TRUE, '[
           "MONDAY"
       ]', '13:00:00', '13:05:00', 'Neu2'),
       ('739a7c26-fb23-41c4-bbcf-26534bd13624', 4, 'Neu4', 'Neu4', '[
           "WOECHENTLICH"
       ]', 0.00, TRUE, '[
           "THURSDAY",
           "FRIDAY"
       ]', '13:10:00', '13:11:00', 'Neu4'),
       ('213db7ac-a532-4890-9b64-173781f5e1ed', 4, 'NEU', 'NEU', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 22.00, TRUE, '[
           "THURSDAY"
       ]', '12:00:00', '12:30:00', 'NEU'),
       ('45435b75-92fd-4ff5-9f5a-962a4c48ed35', 5, 'NEU', 'NEU', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 22.00, TRUE, '[
           "THURSDAY"
       ]', '12:00:00', '12:30:00', 'NEU'),
       ('a85e58c4-ce9d-4db8-9f41-011566df9010', 5, 'Neu5a', 'Neu5a', '[
           "WOECHENTLICH"
       ]', 0.00, TRUE, '[
           "THURSDAY"
       ]', '15:00:00', '16:00:00', 'Neu5a'),
       ('0d22d778-971e-4432-b3d4-4a82b98aee03', 5, 'Neu2', 'Neu2', '[
           "WOECHENTLICH"
       ]', 10.00, TRUE, '[
           "MONDAY"
       ]', '13:00:00', '13:05:00', 'Neu2'),
       ('ff0efb51-fbdd-4e8c-9e7c-6371567a6b05', 5, 'Neu3', 'Neu3', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 1.00, FALSE, '[
           "TUESDAY",
           "WEDNESDAY",
           "THURSDAY"
       ]', '13:10:00', '13:15:00', 'Neu3'),
       ('d3e9ce16-882a-474c-aa50-579714817096', 5, 'Nachmittag', 'Aprés midi', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 3.00, FALSE, '[
           "MONDAY",
           "TUESDAY"
       ]', '14:00:00', '17:00:00', 'Nachmittag'),
       ('d000a4c7-052a-45e2-a1b9-9919390e0769', 5, 'Morgen', 'Matain', '[
           "WOECHENTLICH"
       ]', 5.00, TRUE, '[
           "MONDAY",
           "TUESDAY",
           "WEDNESDAY",
           "THURSDAY",
           "FRIDAY"
       ]', '07:00:00', '12:00:00', 'Morgen'),
       ('e9e97fc7-8681-4526-9372-7fd449be87e9', 5, 'Neu5b', 'Neu5b', '[
           "WOECHENTLICH",
           "ALLE_ZWEI_WOCHEN"
       ]', 1.00, FALSE, '[
           "MONDAY"
       ]', '18:00:00', '18:05:00', 'Neu5b'),
       ('df57287d-cb4e-4f73-8e13-afb890252987', 5, 'Neu4', 'Neu4', '[
           "WOECHENTLICH"
       ]', 0.00, TRUE, '[
           "THURSDAY",
           "FRIDAY"
       ]', '13:10:00', '13:11:00', 'Neu4');

INSERT INTO anmeldung (id, institutionid, refnr, periodevon, periodebis, status, version, abholung,
                       abweichungzweitessemester, anmeldungzurueckgezogen, bemerkung, eintrittsdatum, eventtimestamp,
                       freigegebenam, gesuchsteller, kind, planklasse, module, gesuchsteller2, tarife)
VALUES (2, '5', '21.000142.064.1.1', '2021-08-01', '2022-07-31',
        'SCHULAMT_ANMELDUNG_AUSGELOEST', 0, NULL, FALSE, FALSE, NULL, '2021-08-01', '2022-03-23 11:52:02.000000',
        '2021-12-06', '{
        "email": "heinrich.mueller@example.com",
        "mobile": null,
        "adresse": {
            "ort": "Bern",
            "plz": "3005",
            "land": "CH",
            "strasse": "Haltenweg",
            "hausnummer": "15",
            "adresszusatz": null
        },
        "telefon": null,
        "vorname": "Hans Rudolf",
        "nachname": "Meyer",
        "geschlecht": "MAENNLICH",
        "geburtsdatum": "1986-02-03",
        "telefonAusland": null
    }', '{
        "vorname": "Nuria",
        "nachname": "Meyer",
        "geschlecht": "WEIBLICH",
        "geburtsdatum": "2017-10-21"
    }', '1', '[
        {
            "fremdId": "Morgen",
            "modulId": "5fe1fd98-8cc2-49db-ad3f-25ac059d3816",
            "intervall": "WOECHENTLICH",
            "wochentag": "MONDAY"
        },
        {
            "fremdId": "Neu4",
            "modulId": "739a7c26-fb23-41c4-bbcf-26534bd13624",
            "intervall": "WOECHENTLICH",
            "wochentag": "THURSDAY"
        },
        {
            "fremdId": "Neu3",
            "modulId": "4f0938ef-a5f6-44f2-8205-1c5a82a1f9f3",
            "intervall": "WOECHENTLICH",
            "wochentag": "THURSDAY"
        }
    ]', '{
        "email": "hans.muster@gmail.com",
        "mobile": null,
        "adresse": null,
        "telefon": null,
        "vorname": "Katrhin",
        "nachname": "Meyer",
        "geschlecht": "WEIBLICH",
        "geburtsdatum": "1986-02-28",
        "telefonAusland": null
    }', NULL),
       (3, '5', '21.000142.064.1.1', '2021-08-01', '2022-07-31',
        'SCHULAMT_ANMELDUNG_UEBERNOMMEN', 0, NULL, FALSE, FALSE, NULL, '2021-08-01', '2022-03-23 11:52:12.000000',
        '2021-12-06', '{
           "email": "heinrich.mueller@example.com",
           "mobile": null,
           "adresse": {
               "ort": "Bern",
               "plz": "3005",
               "land": "CH",
               "strasse": "Haltenweg",
               "hausnummer": "15",
               "adresszusatz": null
           },
           "telefon": null,
           "vorname": "Hans Rudolf",
           "nachname": "Meyer",
           "geschlecht": "MAENNLICH",
           "geburtsdatum": "1986-02-03",
           "telefonAusland": null
       }', '{
           "vorname": "Nuria",
           "nachname": "Meyer",
           "geschlecht": "WEIBLICH",
           "geburtsdatum": "2017-10-21"
       }', '1', '[
           {
               "fremdId": "Morgen",
               "modulId": "5fe1fd98-8cc2-49db-ad3f-25ac059d3816",
               "intervall": "WOECHENTLICH",
               "wochentag": "MONDAY"
           },
           {
               "fremdId": "Neu4",
               "modulId": "739a7c26-fb23-41c4-bbcf-26534bd13624",
               "intervall": "WOECHENTLICH",
               "wochentag": "THURSDAY"
           },
           {
               "fremdId": "Neu3",
               "modulId": "4f0938ef-a5f6-44f2-8205-1c5a82a1f9f3",
               "intervall": "ALLE_ZWEI_WOCHEN",
               "wochentag": "WEDNESDAY"
           },
           {
               "fremdId": "Neu3",
               "modulId": "4f0938ef-a5f6-44f2-8205-1c5a82a1f9f3",
               "intervall": "WOECHENTLICH",
               "wochentag": "THURSDAY"
           }
       ]', '{
           "email": "hans.muster@gmail.com",
           "mobile": null,
           "adresse": null,
           "telefon": null,
           "vorname": "Katrhin",
           "nachname": "Meyer",
           "geschlecht": "WEIBLICH",
           "geburtsdatum": "1986-02-28",
           "telefonAusland": null
       }', '{
           "tarifZeitabschnitte": [
               {
                   "bis": "2021-12-31",
                   "von": "2021-08-01",
                   "familienGroesse": 3,
                   "tarifPaedagogisch": {
                       "totalKostenProWoche": 66.4,
                       "betreuungsKostenProStunde": 12.24,
                       "betreuungsMinutenProWoche": 301,
                       "verpflegungsKostenProWoche": 5,
                       "verpflegungsKostenVerguenstigung": 0
                   },
                   "massgebendesEinkommen": 41600.0,
                   "tarifNichtPaedagogisch": {
                       "totalKostenProWoche": 2.21,
                       "betreuungsKostenProStunde": 6.11,
                       "betreuungsMinutenProWoche": 7,
                       "verpflegungsKostenProWoche": 1.5,
                       "verpflegungsKostenVerguenstigung": 0
                   }
               },
               {
                   "bis": "2022-07-31",
                   "von": "2022-01-01",
                   "familienGroesse": 3,
                   "tarifPaedagogisch": {
                       "totalKostenProWoche": 8.91,
                       "betreuungsKostenProStunde": 0.78,
                       "betreuungsMinutenProWoche": 301,
                       "verpflegungsKostenProWoche": 5,
                       "verpflegungsKostenVerguenstigung": 0
                   },
                   "massgebendesEinkommen": 41600.0,
                   "tarifNichtPaedagogisch": {
                       "totalKostenProWoche": 1.59,
                       "betreuungsKostenProStunde": 0.78,
                       "betreuungsMinutenProWoche": 7,
                       "verpflegungsKostenProWoche": 1.5,
                       "verpflegungsKostenVerguenstigung": 0
                   }
               }
           ],
           "tarifeDefinitivAkzeptiert": true
       }');
-- endregion
