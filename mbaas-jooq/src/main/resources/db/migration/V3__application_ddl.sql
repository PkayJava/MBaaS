CREATE TABLE application (

  application_id      VARCHAR(100) NOT NULL,

  owner_user_id       VARCHAR(100) NOT NULL,

  description         VARCHAR(255),
  name                VARCHAR(255),

  server_url          VARCHAR(255),
  push_application_id VARCHAR(255),
  master_secret       VARCHAR(255),

  security            VARCHAR(15)  NOT NULL,
  date_created        DATETIME,

  optimistic          INT(11)      NOT NULL DEFAULT 0,

  INDEX (name),
  INDEX (description),
  INDEX (owner_user_id),
  PRIMARY KEY (application_id)

);