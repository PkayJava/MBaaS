CREATE TABLE desktop (

  desktop_id    VARCHAR(100) NOT NULL,

  owner_user_id VARCHAR(100),
  date_created  DATETIME,
  date_seen     DATETIME,
  session_id    VARCHAR(200) NOT NULL,
  user_agent    VARCHAR(255),
  client_ip     VARCHAR(30),

  optimistic    INT(11) DEFAULT 0,

  INDEX (owner_user_id),
  INDEX (date_created),
  INDEX (date_seen),
  UNIQUE KEY (session_id),
  INDEX (user_agent),
  INDEX (client_ip),
  INDEX (optimistic),
  PRIMARY KEY (desktop_id)
);