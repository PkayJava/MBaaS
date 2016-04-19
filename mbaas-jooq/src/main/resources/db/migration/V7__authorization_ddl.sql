CREATE TABLE authorization (

  -- code
  authorization_id VARCHAR(100) NOT NULL,

  date_created     DATETIME     NOT NULL,
  time_to_live     INT(11)      NOT NULL,

  application_id   VARCHAR(100) NOT NULL,
  client_id        VARCHAR(100) NOT NULL,
  state            VARCHAR(100) NOT NULL,
  owner_user_id    VARCHAR(100),

  optimistic       INT(11) DEFAULT 0,

  INDEX (date_created),
  INDEX (time_to_live),
  INDEX (application_id),
  INDEX (client_id),
  INDEX (state),
  INDEX (owner_user_id),
  INDEX (optimistic),
  PRIMARY KEY (authorization_id)
);