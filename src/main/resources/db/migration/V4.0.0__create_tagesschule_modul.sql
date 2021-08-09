CREATE TABLE gesuchsperiode (
	id          VARCHAR(255) NOT NULL
		CONSTRAINT gesuchsperiode_pkey
			PRIMARY KEY,
	gueltigab  DATE         NOT NULL,
	gueltigbis DATE         NOT NULL
);

CREATE TABLE modul (
	id                   VARCHAR(255)   NOT NULL
		CONSTRAINT institution_module_pkey
			PRIMARY KEY,
	bezeichnungde       VARCHAR(255)   NOT NULL,
	bezeichnungfr       VARCHAR(255)   NOT NULL,
	zeitvon             TIME           NOT NULL,
	zeitbis             TIME           NOT NULL,
	wochentage           JSONB          NOT NULL,
	intervall           VARCHAR(100)   NOT NULL,
	padaegogischbetreut BOOLEAN        NOT NULL,
	verpflegungskosten  NUMERIC(19, 2) NOT NULL,
	gesuchsperiode_id       VARCHAR(255)   NOT NULL
		CONSTRAINT gesuchsperiode_fk
			REFERENCES gesuchsperiode,
	institution_id          VARCHAR(255)   NOT NULL
		CONSTRAINT institution_fk
			REFERENCES institution
);