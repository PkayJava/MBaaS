CREATE TABLE javascript (

  javascript_id    VARCHAR(100) NOT NULL,
  path             VARCHAR(50)  NOT NULL,
  description      VARCHAR(255) NOT NULL,
  script           TEXT,
  date_created     DATETIME     NOT NULL DEFAULT NOW(),
  security         VARCHAR(15)  NOT NULL,
  user_id          VARCHAR(100),
  application_code VARCHAR(100) NOT NULL,

  UNIQUE KEY (path),
  INDEX (description),
  FULLTEXT (script),
  INDEX (date_created),
  INDEX (security),
  INDEX (user_id),
  INDEX (application_code),
  PRIMARY KEY (javascript_id)
);