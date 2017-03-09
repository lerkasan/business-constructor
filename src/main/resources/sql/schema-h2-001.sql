CREATE TABLE role (
  id      IDENTITY     NOT NULL,
  title   VARCHAR(255) NOT NULL,
  CONSTRAINT role_id PRIMARY KEY (id),
  UNIQUE (title)
);

CREATE TABLE user (
  id                  IDENTITY         NOT NULL,
  username            VARCHAR(255)     NOT NULL,
  first_name          VARCHAR(255),
  middle_name         VARCHAR(255),
  last_name           VARCHAR(255),
  email               VARCHAR(255)     NOT NULL,
  password_hash       VARCHAR(60)      NOT NULL,
  creation_timestamp  TIMESTAMP        NOT NULL,
  CONSTRAINT user_id  PRIMARY KEY (id),
  UNIQUE (username),
  UNIQUE (email)
);

CREATE TABLE user_role (
  id          IDENTITY    NOT NULL,
  user_id     BIGINT      NOT NULL,
  role_id     BIGINT      NOT NULL,
  CONSTRAINT user_role_id PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES role(id)
    ON DELETE CASCADE
);

CREATE TABLE permit_type (
  id      IDENTITY      NOT NULL,
  name    VARCHAR(255)  NOT NULL,
  CONSTRAINT permit_type_id PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE permit (
  id                    IDENTITY        NOT NULL,
  name                  VARCHAR(1023)   NOT NULL,
  permit_type_id        BIGINT          NOT NULL,
  legal_document_id     BIGINT          NOT NULL,
  form_id               BIGINT          NOT NULL,
  number                VARCHAR(11)     NOT NULL,
  file_example          BLOB,
  term                  LONGVARCHAR     NOT NULL,
  propose               LONGVARCHAR     NOT NULL,
  status                TINYINT         NOT NULL,
  CONSTRAINT permit_id PRIMARY KEY (id),
  FOREIGN KEY (permit_type_id) REFERENCES permit_type(id)
    ON DELETE CASCADE,
  UNIQUE (name)
);

CREATE TABLE procedure_type (
  id      IDENTITY        NOT NULL,
  name    VARCHAR(255)    NOT NULL,
  CONSTRAINT procedure_type_id PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE procedure_ (
  id                  IDENTITY          NOT NULL,
  name                VARCHAR(2048)     NOT NULL,
  reason              LONGVARCHAR       NOT NULL,
  result              VARCHAR(2048)     NOT NULL,
  permit_id           BIGINT            NOT NULL,
  procedure_type_id   BIGINT            NOT NULL,
  tool_id             BIGINT            NOT NULL,
  cost                LONGVARCHAR       NOT NULL,
  term                LONGVARCHAR       NOT NULL,
  method              VARCHAR(2048)     NOT NULL,
  decision            LONGVARCHAR       NOT NULL,
  deny                LONGVARCHAR       NOT NULL,
  abuse               LONGVARCHAR       NOT NULL,
  CONSTRAINT procedure_id PRIMARY KEY (id),
  FOREIGN KEY (permit_id) REFERENCES permit(id),
  FOREIGN KEY (procedure_type_id) REFERENCES procedure_type(id)
    ON DELETE CASCADE,
  UNIQUE (name)
);

CREATE TABLE procedure_document (
  id                IDENTITY        NOT NULL,
  name              VARCHAR (255)   NOT NULL,
  procedure_id      BIGINT          NOT NULL,
  example_file      BLOB,
  CONSTRAINT procedure_id PRIMARY KEY (id),
  FOREIGN KEY (procedure_id ) REFERENCES procedure_(id)
    ON DELETE CASCADE,
  UNIQUE (name)
);

CREATE TABLE question (
  id            IDENTITY          NOT NULL,
  text          VARCHAR(1000)     NOT NULL,
  input_type    VARCHAR(255)      NOT NULL,
  CONSTRAINT question_id PRIMARY KEY (id)
);

CREATE TABLE option_ (
  id                  IDENTITY      NOT NULL,
  title               VARCHAR(500)  NOT NULL,
  question_id         BIGINT        NOT NULL,
  procedure_id        BIGINT,
  next_question_id    BIGINT,
  CONSTRAINT option_id PRIMARY KEY (id),
  FOREIGN KEY (question_id) REFERENCES question(id)
    ON DELETE CASCADE,
  FOREIGN KEY (next_question_id) REFERENCES question(id),
  UNIQUE (title, question_id)
);

CREATE TABLE legal_document (
  id                      IDENTITY            NOT NULL,
  id_rada                 VARCHAR(50)         NOT NULL  UNIQUE,
  id_liga                 VARCHAR(24)         NOT NULL,
  id_state                INTEGER             NOT NULL  UNIQUE,
  date_pub                INTEGER             NOT NULL  UNIQUE,
  date_add                INTEGER             NOT NULL,
  number_pub              VARCHAR(255)        NOT NULL  UNIQUE,
  title                   VARCHAR(1025)       NOT NULL,
  number_rada             VARCHAR(255)        NOT NULL,
  number_mj               VARCHAR(65)         NOT NULL,
  in_rada                 TINYINT             NOT NULL  UNIQUE,
  in_liga                 TINYINT             NOT NULL  UNIQUE,
  in_brdo                 TINYINT             NOT NULL  UNIQUE,
  auto_liga               TINYINT             NOT NULL,
  auto_brdo               TINYINT             NOT NULL,
  regulation              INTEGER             NOT NULL  UNIQUE,
  manual_sector           VARCHAR(96)         NOT NULL,
  tech_regulation         INTEGER             NOT NULL,
  CONSTRAINT legal_document_id PRIMARY KEY (id)
);
