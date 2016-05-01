CREATE TABLE javascript (

  javascript_id  VARCHAR(100) NOT NULL,
  path           VARCHAR(50)  NOT NULL,
  description    VARCHAR(255) NOT NULL,
  script         TEXT,
  date_created   DATETIME     NOT NULL DEFAULT NOW(),
  security       VARCHAR(15)  NOT NULL,
  owner_user_id  VARCHAR(100) NOT NULL,
  application_id VARCHAR(100) NOT NULL,

  UNIQUE KEY (path),
  INDEX (description),
  FULLTEXT (script),
  INDEX (date_created),
  INDEX (security),
  INDEX (owner_user_id),
  INDEX (application_id),
  PRIMARY KEY (javascript_id)
);