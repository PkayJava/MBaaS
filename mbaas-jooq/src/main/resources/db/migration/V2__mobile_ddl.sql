CREATE TABLE mobile (

  mobile_id               VARCHAR(100) NOT NULL,

  user_id                 VARCHAR(100) NOT NULL,
  client_id               VARCHAR(100) NOT NULL,
  application_id          VARCHAR(100) NOT NULL,
  date_created            DATETIME,
  date_seen               DATETIME,

  user_agent              VARCHAR(255),

  device_token            VARCHAR(255),
  device_type             VARCHAR(255),
  device_alias            VARCHAR(255),
  device_operating_system VARCHAR(80),
  device_os_version       VARCHAR(50),

  client_ip               VARCHAR(30),

  extra                   BLOB,
  -- device_category_{name} : boolean

  optimistic              INT(11)      NOT NULL DEFAULT 0,

  INDEX (date_created),
  INDEX (client_ip),
  INDEX (date_seen),
  INDEX (user_agent),
  INDEX (device_token),
  INDEX (user_id),
  INDEX (application_id),
  INDEX (client_id),
  PRIMARY KEY (mobile_id)

);