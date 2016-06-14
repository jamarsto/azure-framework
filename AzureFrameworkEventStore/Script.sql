DROP TABLE EVENT_STORE;

CREATE TABLE EVENT_STORE (
	ID UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
	BUCKET_ID VARCHAR(1024) NOT NULL,
	STREAM_ID CHAR(36) NOT NULL,
	CHANGE_SET_ID CHAR(36) NOT NULL,
	EVENT_CREATED DATETIME DEFAULT GETUTCDATE() NOT NULL,
	EVENT_CLASS_NAME VARCHAR(1024) NOT NULL,
	EVENT_VERSION BIGINT NOT NULL,
	EVENT VARCHAR(max) NOT NULL
);

CREATE UNIQUE INDEX UK_EVENTST_BI_SI_EV ON EVENT_STORE(BUCKET_ID, STREAM_ID, EVENT_VERSION);

CREATE INDEX K_EVENTST_BI_SI_ECN_EV ON EVENT_STORE(BUCKET_ID, STREAM_ID, EVENT_CLASS_NAME, EVENT_VERSION);

CREATE INDEX K_EVENTST_BI_SI_CSI_EV ON EVENT_STORE(BUCKET_ID, STREAM_ID, CHANGE_SET_ID, EVENT_VERSION);

CREATE INDEX K_EVENTST_BI_SI_CSI_ECN_EV ON EVENT_STORE(BUCKET_ID, STREAM_ID, CHANGE_SET_ID, EVENT_CLASS_NAME, EVENT_VERSION);
