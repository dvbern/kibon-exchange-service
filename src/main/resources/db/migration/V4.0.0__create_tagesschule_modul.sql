CREATE TABLE gesuchsperiode (
	id          VARCHAR(255) NOT NULL
		CONSTRAINT gesuchsperiode_pkey
			PRIMARY KEY,
	gueltig_ab  DATE         NOT NULL,
	gueltig_bis DATE         NOT NULL
);

CREATE TABLE modul (
	id                   VARCHAR(255)   NOT NULL
		CONSTRAINT institution_module_pkey
			PRIMARY KEY,
	bezeichnung_de       VARCHAR(255)   NOT NULL,
	bezeichnung_fr       VARCHAR(255)   NOT NULL,
	zeit_von             TIME           NOT NULL,
	zeit_bis             TIME           NOT NULL,
	wochentage           JSONB          NOT NULL,
	intervalle           VARCHAR(100)   NOT NULL,
	padaegogisch_betreut BOOLEAN        NOT NULL,
	verpflegungs_kosten  NUMERIC(19, 2) NOT NULL,
	gesuchsperiode       VARCHAR(255)   NOT NULL
		CONSTRAINT gesuchsperiode_fk
			REFERENCES gesuchsperiode,
	institution          VARCHAR(255)   NOT NULL
		CONSTRAINT institution_fk
			REFERENCES institution
);