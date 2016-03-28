CREATE TABLE client (

  client_id           VARCHAR(100) NOT NULL,

  application_id      VARCHAR(100),

  application_user_id VARCHAR(100),
  owner_user_id       VARCHAR(100),
  secret              VARCHAR(100) NOT NULL,
  date_created        DATETIME,

  push_variant_id     VARCHAR(255),
  push_secret         VARCHAR(255),
  push_gcm_sender_id  VARCHAR(255),

  name                VARCHAR(255),
  description         VARCHAR(255),
  security            VARCHAR(15)  NOT NULL,

  optimistic          INT(11)      NOT NULL DEFAULT 0,

  UNIQUE (secret),
  UNIQUE (name, application_id),
  INDEX (name),
  INDEX (description),
  INDEX (application_id),
  INDEX (owner_user_id),
  INDEX (application_user_id),
  PRIMARY KEY (client_id)

);