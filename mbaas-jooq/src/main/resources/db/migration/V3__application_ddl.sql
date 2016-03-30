CREATE TABLE application (

  application_id      VARCHAR(100) NOT NULL,

  owner_user_id       VARCHAR(100) NOT NULL,

  description         VARCHAR(255),
  name                VARCHAR(255),

  auto_registration   BIT(1)       NOT NULL,

  push_server_url     VARCHAR(255),
  push_application_id VARCHAR(255),
  push_master_secret  VARCHAR(255),

  oauth_roles         VARCHAR(255),

  extra               BLOB,
  -- oauth_role_{name} : true | false

  security            VARCHAR(15)  NOT NULL,
  date_created        DATETIME,

  optimistic          INT(11)      NOT NULL DEFAULT 0,

  INDEX (name),
  INDEX (description),
  INDEX (owner_user_id),
  PRIMARY KEY (application_id)

);