INSERT INTO client (clientid, grantedsince, institutionid)
VALUES ('kitAdmin', now(), '1'),
	   ('kitAdmin', now() - INTERVAL '3 days', '2'),
	   ('CSE', now() - INTERVAL '3 days', '1');

INSERT INTO institution (id, adresszusatz, hausnummer, land, ort, plz, strasse, name, traegerschaft)
VALUES ('1', NULL, '21', 'CH', 'Bern', '3006', 'Nussbaumstrasse', 'DV Kids', 'DV Bern AG'),
	   ('2', NULL, '21', 'CH', 'Bern', '3022', 'Nussbaumstrasse', 'DV Juniors', 'DV Bern');

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
