CREATE TABLE authorization (

  -- code
  authorization_id VARCHAR(100) NOT NULL,

  date_created     DATETIME     NOT NULL,
  time_to_live     INT(11)      NOT NULL,

  application_id   VARCHAR(100) NOT NULL,
  client_id        VARCHAR(100) NOT NULL,
  state            VARCHAR(100) NOT NULL,
  owner_user_id    VARCHAR(100),

  optimistic       INT(11)      NOT NULL DEFAULT 0,

  INDEX (state),
  INDEX (application_id),
  INDEX (client_id),
  INDEX (owner_user_id),
  PRIMARY KEY (authorization_id)

);