CREATE TABLE persistent_logins (
    username    VARCHAR(64)   NOT NULL,
    series      VARCHAR(64)   NOT NULL,
    token       VARCHAR(64)   NOT NULL,
    last_used   TIMESTAMP     NOT NULL,
    PRIMARY KEY (series)
);

CREATE TABLE verification_token (
    id                IDENTITY        NOT NULL,
    token             VARCHAR(64)     NOT NULL,
    user_id           BIGINT          NOT NULL,
    expiry_timestamp  TIMESTAMP       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (token),
    FOREIGN KEY (user_id) REFERENCES user(id)
      ON DELETE CASCADE
);

ALTER TABLE user
  ADD enabled  BOOLEAN  NOT NULL;