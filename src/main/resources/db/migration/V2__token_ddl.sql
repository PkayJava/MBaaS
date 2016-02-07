CREATE TABLE token (

  token_id     VARCHAR(255) NOT NULL,

  user_id      INT(11),
  date_created DATETIME,
  date_seen    DATETIME,

  push_token   VARCHAR(255),

  deleted      BIT(1)       NOT NULL DEFAULT 0,
  optimistic   INT(11)      NOT NULL DEFAULT 0,

  INDEX (user_id),
  INDEX (deleted),
  PRIMARY KEY (token_id)

);