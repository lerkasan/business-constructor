CREATE TABLE role (
  id      BIGINT        NOT NULL  AUTO_INCREMENT,
  title   VARCHAR(255)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (title)
);

CREATE TABLE user (
  id                  BIGINT          NOT NULL  AUTO_INCREMENT,
  username            VARCHAR(255),
  first_name          VARCHAR(255),
  middle_name         VARCHAR(255),
  last_name           VARCHAR(255),
  email               VARCHAR(255)    NOT NULL,
  password_hash       VARCHAR(60)     NOT NULL,
  creation_timestamp  TIMESTAMP       NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (email),
  UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS user_role (
  user_id    BIGINT   NOT NULL,
  role_id    BIGINT   NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES role(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS permit_type (
  id      BIGINT        NOT NULL  AUTO_INCREMENT,
  name    VARCHAR(255)  NOT NULL,
  CONSTRAINT permit_type_id PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS permit (
  id                 BIGINT          NOT NULL  AUTO_INCREMENT,
  name               VARCHAR(750)    NOT NULL,
  permit_type_id     BIGINT          NOT NULL,
  legal_document_id  BIGINT          NOT NULL,
  form_id            BIGINT          NOT NULL,
  number             VARCHAR(11)     NOT NULL,
  file_example       BLOB,
  term               varchar(3000)   NOT NULL,
  propose            varchar(3000)   NOT NULL,
  status             TINYINT         NOT NULL,
  CONSTRAINT permit_id PRIMARY KEY (id),
  FOREIGN KEY (permit_type_id) REFERENCES permit_type(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS procedure_type (
  id      BIGINT        NOT NULL  AUTO_INCREMENT,
  name    VARCHAR(255)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)

);

CREATE TABLE IF NOT EXISTS procedure_ (
  id                  BIGINT          NOT NULL  AUTO_INCREMENT,
  name                VARCHAR(2048)   NOT NULL,
  reason              VARCHAR(2048)   NOT NULL,
  result              VARCHAR(2048)   NOT NULL,
  tool_id             BIGINT          NOT NULL,
  permit_id           BIGINT          NOT NULL,
  procedure_type_id   BIGINT          NOT NULL,
  cost                VARCHAR(2048)   NOT NULL,
  term                VARCHAR(4096)   NOT NULL,
  method              VARCHAR(2048)   NOT NULL,
  decision            VARCHAR(2048)   NOT NULL,
  deny                VARCHAR(3072)   NOT NULL,
  abuse               VARCHAR(2048)   NOT NULL,
  CONSTRAINT procedure_id PRIMARY KEY (id),
  FOREIGN KEY (permit_id) REFERENCES permit(id),
  FOREIGN KEY (procedure_type_id) REFERENCES procedure_type(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question (
  id            BIGINT          NOT NULL   AUTO_INCREMENT,
  text          VARCHAR(3000)   NOT NULL,
  input_type    VARCHAR(255)    NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS option_ (
  id                  BIGINT         NOT NULL   AUTO_INCREMENT,
  title               VARCHAR(500)   NOT NULL,
  question_id         BIGINT         NOT NULL,
  procedure_id        BIGINT,
  next_question_id    BIGINT,
  PRIMARY KEY (id),
  FOREIGN KEY (question_id) REFERENCES question (id)
    ON DELETE CASCADE,
  FOREIGN KEY (next_question_id) REFERENCES question (id)
    ON DELETE SET NULL,
  FOREIGN KEY (procedure_id) REFERENCES procedure_ (id)
    ON DELETE SET NULL,
  UNIQUE (title(255), question_id)
);

CREATE TABLE IF NOT EXISTS procedure_document (
  id              BIGINT         NOT NULL  AUTO_INCREMENT,
  name            VARCHAR(255)   NOT NULL,
  procedure_id    BIGINT         NOT NULL,
  example_file    BLOB,
  CONSTRAINT procedure_id PRIMARY KEY (id),
  FOREIGN KEY (procedure_id) REFERENCES procedure_(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS legal_document (
  id                      BIGINT              NOT NULL  AUTO_INCREMENT,
  id_rada                 VARCHAR(50)         NOT NULL  UNIQUE,
  id_liga                 VARCHAR(24)         NOT NULL,
  id_state                INTEGER             NOT NULL,
  date_pub                INTEGER             NOT NULL,
  date_add                INTEGER             NOT NULL,
  number_pub              VARCHAR(255)        NOT NULL,
  title                   VARCHAR(1025)       NOT NULL,
  number_rada             VARCHAR(255)        NOT NULL,
  number_mj               VARCHAR(65)         NOT NULL,
  in_rada                 TINYINT             NOT NULL,
  in_liga                 TINYINT             NOT NULL,
  in_brdo                 TINYINT             NOT NULL,
  auto_liga               TINYINT             NOT NULL,
  auto_brdo               TINYINT             NOT NULL,
  regulation              INTEGER             NOT NULL,
  manual_sector           VARCHAR(96)         NOT NULL,
  tech_regulation         INTEGER             NOT NULL,
  CONSTRAINT legal_document_id PRIMARY KEY (id)
);
