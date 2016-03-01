CREATE TABLE session (

  session_id   VARCHAR(100) NOT NULL,

  user_id      VARCHAR(100) NOT NULL,
  date_created DATETIME,
  date_seen    DATETIME,

  push_token   VARCHAR(255),

  deleted      BIT(1)       NOT NULL DEFAULT 0,
  optimistic   INT(11)      NOT NULL DEFAULT 0,

  INDEX (user_id),
  INDEX (deleted),
  PRIMARY KEY (session_id)

);