CREATE TABLE authorization (

  authorization_id VARCHAR(100) NOT NULL, -- code
  date_created     DATETIME     NOT NULL,
  time_to_live     INT(11)      NOT NULL,
  application_code VARCHAR(100) NOT NULL,
  client_id        VARCHAR(100) NOT NULL,
  state            VARCHAR(100) NOT NULL,
  user_id          VARCHAR(100),

  INDEX (date_created),
  INDEX (time_to_live),
  INDEX (application_code),
  INDEX (client_id),
  INDEX (state),
  INDEX (user_id),
  PRIMARY KEY (authorization_id)
);