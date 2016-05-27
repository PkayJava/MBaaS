CREATE TABLE query (

  query_id         VARCHAR(100) NOT NULL,
  name             VARCHAR(255) NOT NULL,
  description      VARCHAR(255) NOT NULL,
  script           TEXT         NOT NULL,
  return_type      VARCHAR(50)  NOT NULL,
  return_sub_type  VARCHAR(50),
  date_created     DATETIME     NOT NULL DEFAULT NOW(),
  security         VARCHAR(15)  NOT NULL,
  user_id          VARCHAR(100),
  application_code VARCHAR(100) NOT NULL,

  UNIQUE KEY (name),
  INDEX (description),
  FULLTEXT (script),
  INDEX (return_type),
  INDEX (return_sub_type),
  INDEX (date_created),
  INDEX (security),
  INDEX (user_id),
  INDEX (application_code),
  PRIMARY KEY (query_id)
);