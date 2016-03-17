CREATE TABLE desktop (

  desktop_id    VARCHAR(100) NOT NULL,

  user_id      VARCHAR(100) NOT NULL,
  date_created DATETIME,
  date_seen    DATETIME,
  session_id   VARCHAR(200) NOT NULL,
  user_agent   VARCHAR(255),
  client_ip    VARCHAR(30),

  optimistic   INT(11)      NOT NULL DEFAULT 0,

  INDEX (user_id),
  INDEX (client_ip),
  INDEX (user_agent),
  INDEX (date_created),
  INDEX (date_seen),
  UNIQUE KEY (session_id),
  PRIMARY KEY (desktop_id)

);