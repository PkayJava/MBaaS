CREATE TABLE application (

  application_id      VARCHAR(100) NOT NULL,
  mbaas_user_id       VARCHAR(100) NOT NULL,
  secret              VARCHAR(255) NOT NULL,
  name                VARCHAR(255) NOT NULL,
  code                VARCHAR(255) NOT NULL,
  description         VARCHAR(255),
  push_application_id VARCHAR(255),
  push_master_secret  VARCHAR(255),
  oauth_roles         VARCHAR(255),
  security            VARCHAR(15)  NOT NULL,
  date_created        DATETIME,

  INDEX (mbaas_user_id),
  INDEX (description),
  INDEX (name),
  UNIQUE (code),
  UNIQUE (secret),
  INDEX (push_application_id),
  INDEX (push_master_secret),
  INDEX (oauth_roles),
  INDEX (security),
  INDEX (date_created),
  PRIMARY KEY (application_id)
);