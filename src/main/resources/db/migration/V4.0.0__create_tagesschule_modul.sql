CREATE TABLE tagesschulemodule (
	id             BIGSERIAL    NOT NULL
		CONSTRAINT tagesschulemodule_pkey PRIMARY KEY,
	periodebis     DATE         NOT NULL,
	periodevon     DATE         NOT NULL,
	institution_id VARCHAR(255) NOT NULL
		CONSTRAINT institution_fk REFERENCES institution,
	CONSTRAINT tagesschule_module_uc1
		UNIQUE (institution_id, periodevon, periodebis)
);

CREATE TABLE modul (
	id                      VARCHAR(255) NOT NULL
		CONSTRAINT modul_pkey PRIMARY KEY,
	parent_id               BIGINT
		CONSTRAINT tagesschule_module_fk REFERENCES tagesschulemodule,
	bezeichnungde           VARCHAR(255),
	bezeichnungfr           VARCHAR(255),
	erlaubteintervalle      JSONB,
	verpflegungskosten      NUMERIC(19, 2),
	wirdpaedagogischbetreut BOOLEAN      NOT NULL,
	wochentage              JSONB,
	zeitvon                 TIME,
	zeitbis                 TIME
);
