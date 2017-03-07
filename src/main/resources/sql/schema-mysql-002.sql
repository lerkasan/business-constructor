CREATE TABLE business_type (
                id          BIGINT         NOT NULL   AUTO_INCREMENT,
                title       VARCHAR(1000)  NOT NULL,
                code_kved   VARCHAR(5)     NOT NULL,
                CONSTRAINT business_type_id PRIMARY KEY (id),
                UNIQUE (code_kved),
                UNIQUE (title(255))
);

CREATE TABLE questionnaire (
                id                  BIGINT          NOT NULL  AUTO_INCREMENT,
                title               VARCHAR(1000)   NOT NULL,
                business_type_id    BIGINT          NOT NULL,
                CONSTRAINT questionnaire_id PRIMARY KEY (id),
                FOREIGN KEY (business_type_id) REFERENCES business_type(id)
                  ON DELETE CASCADE,
                UNIQUE (title(255))
);

ALTER TABLE question
  ADD questionnaire_id  BIGINT  NOT NULL;

ALTER TABLE question
  ADD CONSTRAINT question_questionnaire_fk
  FOREIGN KEY (questionnaire_id) REFERENCES questionnaire (id)
    ON DELETE CASCADE;
