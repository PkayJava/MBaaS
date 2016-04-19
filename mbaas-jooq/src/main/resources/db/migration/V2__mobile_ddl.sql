CREATE TABLE mobile (

  -- refresh token
  mobile_id               VARCHAR(100) NOT NULL,

  owner_user_id           VARCHAR(100),
  client_id               VARCHAR(100),
  application_id          VARCHAR(100),
  date_created            DATETIME,
  date_seen               DATETIME,
  access_token            VARCHAR(100),
  date_token_issued       DATETIME,
  time_to_live            INT(11),
  grant_type              VARCHAR(100),

  user_agent              VARCHAR(255),

  device_token            VARCHAR(255),
  device_type             VARCHAR(255),
  device_alias            VARCHAR(255),
  device_operating_system VARCHAR(80),
  device_os_version       VARCHAR(50),

  client_ip               VARCHAR(30),

  optimistic              INT(11) DEFAULT 0,

  INDEX (owner_user_id),
  INDEX (client_id),
  INDEX (application_id),
  INDEX (date_created),
  INDEX (date_seen),
  INDEX (access_token),
  INDEX (date_token_issued),
  INDEX (time_to_live),
  INDEX (grant_type),
  INDEX (user_agent),
  INDEX (device_token),
  INDEX (device_type),
  INDEX (device_alias),
  INDEX (device_operating_system),
  INDEX (device_os_version),
  INDEX (client_ip),
  INDEX (optimistic),
  PRIMARY KEY (mobile_id)
);