DROP TABLE VIEW_VERSION;

CREATE TABLE VIEW_VERSION (
	ID UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
	REVISION BIGINT NOT NULL,
	AGGREGATE_NAME VARCHAR(1024) NOT NULL,
	VIEW_NAME VARCHAR(1024) NOT NULL,
	AGGREGATE_ID UNIQUEIDENTIFIER NOT NULL,
	VERSION BIGINT NOT NULL
);

CREATE UNIQUE INDEX UK_VIEWV_AN_VN_AI ON VIEW_VERSION(AGGREGATE_NAME, VIEW_NAME, AGGREGATE_ID);