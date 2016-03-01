CREATE TABLE wicket (

  wicket_id    VARCHAR(100) NOT NULL,

  user_id      VARCHAR(100) NOT NULL,
  date_created DATETIME,
  date_seen    DATETIME,
  session_id   VARCHAR(200) NOT NULL,

  optimistic   INT(11)      NOT NULL DEFAULT 0,

  INDEX (user_id),
  UNIQUE KEY (session_id),
  PRIMARY KEY (wicket_id)

);