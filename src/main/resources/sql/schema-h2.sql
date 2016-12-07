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
  CONSTRAINT permit_type_id PRIMARY KEY (id)
);

CREATE UNIQUE INDEX permitTypeNameIndx
ON permit_type (name);


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
  FOREIGN KEY (permit_type_id) REFERENCES permit_type(id)
);

CREATE UNIQUE INDEX permitNameIndx
ON permit (name);

CREATE TABLE question (
                id IDENTITY NOT NULL,
                text VARCHAR(1000) NOT NULL,
                input_type VARCHAR(255) NOT NULL DEFAULT 'SINGLE_CHOICE',
                CONSTRAINT question_id PRIMARY KEY (id)
);

CREATE TABLE option_ (
                id IDENTITY NOT NULL,
                title VARCHAR(500) NOT NULL,
                question_id BIGINT NOT NULL,
                procedure_id BIGINT,
                next_question_id BIGINT,
                CONSTRAINT option_id PRIMARY KEY (id)
);

ALTER TABLE option_ ADD CONSTRAINT option_question_fk
FOREIGN KEY (question_id)
REFERENCES question (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE option_ ADD CONSTRAINT option_next_question_fk
FOREIGN KEY (next_question_id)
REFERENCES question (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE user_role ADD CONSTRAINT user_role_user_id_fk
FOREIGN KEY (user_id)
REFERENCES user (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE user_role ADD CONSTRAINT user_role_role_id_fk
FOREIGN KEY (role_id)
REFERENCES role (id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;