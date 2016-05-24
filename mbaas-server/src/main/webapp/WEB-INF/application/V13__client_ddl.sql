CREATE TABLE client (

  client_id           VARCHAR(100) NOT NULL, -- client id
  application_code    VARCHAR(100),
  application_user_id VARCHAR(100),
  client_secret       VARCHAR(100),
  date_created        DATETIME,
  push_variant_id     VARCHAR(255),
  push_secret         VARCHAR(255),
  push_gcm_sender_id  VARCHAR(255),
  name                VARCHAR(255) NOT NULL,
  description         VARCHAR(255),
  security            VARCHAR(15)  NOT NULL,

  INDEX (application_code),
  INDEX (application_user_id),
  INDEX (application_user_id),
  INDEX (client_secret),
  INDEX (date_created),
  INDEX (push_variant_id),
  INDEX (push_secret),
  INDEX (push_gcm_sender_id),
  INDEX (description),
  INDEX (security),
  UNIQUE (name),
  PRIMARY KEY (client_id)
);