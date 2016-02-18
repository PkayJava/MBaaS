CREATE TABLE role (

  role_id     VARCHAR(100) NOT NULL,

  name        VARCHAR(255) NOT NULL,
  description VARCHAR(255),

  deleted     BIT(1)       NOT NULL DEFAULT 0,
  optimistic  INT(11)      NOT NULL DEFAULT 0,

  INDEX (deleted),
  UNIQUE KEY (name),
  PRIMARY KEY (role_id)

);