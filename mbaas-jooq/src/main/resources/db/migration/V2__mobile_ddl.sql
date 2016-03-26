CREATE TABLE mobile (

  mobile_id      VARCHAR(100) NOT NULL,

  user_id        VARCHAR(100) NOT NULL,
  client_id      VARCHAR(100) NOT NULL,
  application_id VARCHAR(100) NOT NULL,
  date_created   DATETIME,
  date_seen      DATETIME,

  user_agent     VARCHAR(255),

  push_token     VARCHAR(255),
  client_ip      VARCHAR(30),

  extra          BLOB,

  optimistic     INT(11)      NOT NULL DEFAULT 0,

  INDEX (date_created),
  INDEX (client_ip),
  INDEX (date_seen),
  INDEX (user_agent),
  INDEX (push_token),
  INDEX (user_id),
  INDEX (application_id),
  INDEX (client_id),
  PRIMARY KEY (mobile_id)

);