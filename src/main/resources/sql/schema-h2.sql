CREATE TABLE role (
  id    IDENTITY     NOT NULL,
  title VARCHAR(255) NOT NULL,
  CONSTRAINT role_id PRIMARY KEY (id)
);


CREATE UNIQUE INDEX user_role_title_idx
ON role (title);

CREATE TABLE user (
  id                      IDENTITY             NOT NULL,
  username                VARCHAR(255)         NOT NULL,
  first_name              VARCHAR(255),
  middle_name             VARCHAR(255),
  last_name               VARCHAR(255),
  email                   VARCHAR(255)         NOT NULL,
  password_hash           VARCHAR(60)          NOT NULL,
  creation_date           DATE                 NOT NULL,
  CONSTRAINT user_id PRIMARY KEY (id)
);

CREATE UNIQUE INDEX user_username_idx
ON user (username);

CREATE UNIQUE INDEX user_email_idx
ON user (email);

CREATE TABLE user_role (
  id      IDENTITY NOT NULL,
  user_id BIGINT   NOT NULL,
  role_id BIGINT   NOT NULL,
  CONSTRAINT user_role_id PRIMARY KEY (id)
);

CREATE TABLE permit_type (
  id   IDENTITY      NOT NULL,
  name VARCHAR(255)  NOT NULL,
  CONSTRAINT permit_type_id PRIMARY KEY (id),
  UNIQUE (name)
);


CREATE TABLE permit (
  id               IDENTITY      NOT NULL,
  name             VARCHAR(1023) NOT NULL,
  permit_type_id     BIGINT        NOT NULL,
  legal_document_id  BIGINT        NOT NULL,
  form_id           BIGINT        NOT NULL,
  number           VARCHAR(11)   NOT NULL,
  file_example      BLOB,
  term             LONGVARCHAR   NOT NULL,
  propose          LONGVARCHAR   NOT NULL,
  status           TINYINT       NOT NULL,
  CONSTRAINT permit_id PRIMARY KEY (id),
  FOREIGN KEY (permit_type_id) REFERENCES permit_type(id),
  UNIQUE (NAME )
);
