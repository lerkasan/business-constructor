CREATE TABLE persistent_logins (
    username    VARCHAR(64)   NOT NULL,
    series      VARCHAR(64)   NOT NULL,
    token       VARCHAR(64)   NOT NULL,
    last_used   TIMESTAMP     NOT NULL,
    PRIMARY KEY (series)
);

ALTER TABLE user
  ADD enabled  BOOLEAN  NOT NULL;

ALTER TABLE user
  ADD token    VARCHAR(64);