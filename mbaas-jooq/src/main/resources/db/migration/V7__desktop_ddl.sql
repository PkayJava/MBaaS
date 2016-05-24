CREATE TABLE desktop (

  desktop_id          VARCHAR(100) NOT NULL,
  mbaas_user_id       VARCHAR(100),
  application_user_id VARCHAR(100),
  date_created        DATETIME,
  date_seen           DATETIME,
  session_id          VARCHAR(200) NOT NULL,
  user_agent          VARCHAR(255),
  client_ip           VARCHAR(30),

  INDEX (mbaas_user_id),
  INDEX (application_user_id),
  INDEX (date_created),
  INDEX (date_seen),
  UNIQUE KEY (session_id),
  INDEX (user_agent),
  INDEX (client_ip),
  PRIMARY KEY (desktop_id)
);