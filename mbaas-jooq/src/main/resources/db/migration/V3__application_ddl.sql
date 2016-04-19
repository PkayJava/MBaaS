CREATE TABLE application (

  application_id      VARCHAR(100) NOT NULL,

  owner_user_id       VARCHAR(100),

  description         VARCHAR(255),
  name                VARCHAR(255),

  push_application_id VARCHAR(255),
  push_master_secret  VARCHAR(255),

  oauth_roles         VARCHAR(255),

  security            VARCHAR(15)  NOT NULL,
  date_created        DATETIME,

  optimistic          INT(11) DEFAULT 0,

  INDEX (owner_user_id),
  INDEX (description),
  INDEX (name),
  INDEX (push_application_id),
  INDEX (push_master_secret),
  INDEX (oauth_roles),
  INDEX (security),
  INDEX (date_created),
  INDEX (optimistic),
  PRIMARY KEY (application_id)
);