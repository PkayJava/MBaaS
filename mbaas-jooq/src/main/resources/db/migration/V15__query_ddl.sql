CREATE TABLE query (

  query_id        VARCHAR(100) NOT NULL,
  name            VARCHAR(255) NOT NULL,
  description     VARCHAR(255) NOT NULL,
  script          TEXT         NOT NULL,
  return_type     VARCHAR(50)  NOT NULL,
  return_sub_type VARCHAR(50),
  date_created    DATETIME     NOT NULL DEFAULT NOW(),
  security        VARCHAR(15)  NOT NULL,
  owner_user_id   VARCHAR(100),
  optimistic      INT(11)               DEFAULT 0,
  application_id  VARCHAR(100) NOT NULL,

  UNIQUE KEY (name),
  INDEX (description),
  FULLTEXT (script),
  INDEX (return_type),
  INDEX (return_sub_type),
  INDEX (date_created),
  INDEX (security),
  INDEX (owner_user_id),
  INDEX (optimistic),
  INDEX (application_id),
  PRIMARY KEY (query_id)
);