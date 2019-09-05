/*
 What are we doing here?

 Es werden laufend neue Verfügungen freigegeben. Diese Können über verfuegAm & die VerfuegungId problemlos paginiert
 werden, damit use cases wie 'alle Verfügungen seit gestern', 'alle Verfügungen an diesem Datum' oder
 'Verfügungen mit ID höher als [zuletztErhalteneId]' umgesetzt werden können.

 Es gibt da aber ein nicht triviales Problem: ein Client darf nur auf eine Verfügung zugreifen, wenn er dafür
 berechtigt worden ist. Sobald er berechtigt ist, darf er natürlich auch Verfügungen laden, die zuvor für die
 freigegebene Institution verfügt wurde. Wir brauchen also ein neues Datum, das sich aus dem verfuegtAm und dem
 grantedSince Datum zusammen setzt: since = max(grantedSince, verfuegtAm).
 Über dieses berechnete Datum müssen wir aber eine konsistente Sortierung garantieren, damit die Pagination funktioniert.

 Eine einfache View war für diesen Use Case viel zu langsam (bei > 4 Millionen Verfügungen kann das > 1 Min dauern).
 Eine Materialized View ist zwar super schnell, braucht aber lange zum Aufbauen. Mit jedem neuen Client, wird die
 Datenmenge vervielfacht und Postgres kann nur die ganze view refreshen.

 Es wird deshalb eine neue clientverfuegung Tabelle erstellt, in welcher das Verfügbarkeitsdatum, eine sequentielle ID
 und die wichtigsten Filter gespeichert werden, damit wir die Daten schnell abrufen können.
 Die Tabelle wird automatisch über Trigger von den client und verfuegung Tabellen populiert. Dank den Trigger werden
 nur die Daten hinzugefügt, welche tatsächlich neu sind, was natürlich viel schneller ist als ein full refresh einer
 materialized view.

 Zu beachten ist, dass die Trigger synchron arbeiten und deshalb eine Auswirkung haben auf die Verarbeitung von client
 und verfuegung inserts. Es kann z.B. einige Sekunden dauern einen client hinzuzufügen, wenn wir bereits viele
 Verfügungen dieser Institution gespeichert haben.
 Man kann das aber bei Bedarf weiter optimieren und statt direkt in die client_verfuegugne Tabelle zu schreiben nur
 ein NOTIFY absetzen: https://stackoverflow.com/a/29447328
 */

-- region new verfuegung trigger

CREATE FUNCTION verfuegung_insert() RETURNS TRIGGER
	SECURITY DEFINER
	LANGUAGE plpgsql
AS
$$
BEGIN
	INSERT INTO clientverfuegung (id, clientname, institutionid, verfuegung_id, since)
		(SELECT nextval('clientverfuegung_id_seq'), c.clientname, c.institutionid, new.id,
				greatest(c.grantedsince, new.verfuegtam)
		 FROM client c
		 WHERE c.institutionid = new.institutionid);
	RETURN new;
END;
$$;

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
$$
BEGIN
	INSERT INTO clientverfuegung (id, clientname, institutionid, verfuegung_id, since)
		(SELECT nextval('clientverfuegung_id_seq'), new.clientname, new.institutionid, v.id,
				greatest(new.grantedsince, v.verfuegtam)
		 FROM verfuegung v
		 WHERE v.institutionid = new.institutionid
		 ORDER BY greatest(new.grantedsince, v.verfuegtam), v.id);
	RETURN new;
END;
$$;

CREATE TRIGGER client_insert
	AFTER INSERT
	ON client
	FOR EACH ROW
EXECUTE PROCEDURE client_insert();
-- endregion

-- region populate clientverfuegung
-- the import.sql files are imported before FlyWay, thus insert manually into clientverfuegung
INSERT INTO clientverfuegung (id, clientname, institutionid, verfuegung_id, since)
	(SELECT nextval('clientverfuegung_id_seq'), c.clientname, c.institutionid, v.id,
			greatest(c.grantedsince, v.verfuegtam)
	 FROM client c
		  INNER JOIN verfuegung v ON c.institutionid = v.institutionid
	 ORDER BY greatest(c.grantedsince, v.verfuegtam), v.id);

-- endregion
