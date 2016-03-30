CREATE TABLE client (

  -- client id
  client_id           VARCHAR(100) NOT NULL,

  application_id      VARCHAR(100),
  user_id             VARCHAR(100),

  application_user_id VARCHAR(100),

  date_created        DATETIME,

  push_variant_id     VARCHAR(255),
  push_secret         VARCHAR(255),
  push_gcm_sender_id  VARCHAR(255),

  name                VARCHAR(255),
  description         VARCHAR(255),
  security            VARCHAR(15)  NOT NULL,

  optimistic          INT(11)      NOT NULL DEFAULT 0,

  UNIQUE (name, application_id),
  INDEX (name),
  INDEX (description),
  INDEX (application_id),
  INDEX (user_id),
  INDEX (application_user_id),
  PRIMARY KEY (client_id)

);