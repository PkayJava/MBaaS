CREATE TABLE application (

  application_id     VARCHAR(255) NOT NULL,

  user_id      INT(11),
  code         VARCHAR(255),

  created_date DATETIME,

  extra  blob,
  deleted      BIT(1)       NOT NULL DEFAULT 0,
  optimistic   INT(11)      NOT NULL DEFAULT 0,

  UNIQUE (code),
  INDEX (user_id),
  INDEX (deleted),
  PRIMARY KEY (application_id)

);