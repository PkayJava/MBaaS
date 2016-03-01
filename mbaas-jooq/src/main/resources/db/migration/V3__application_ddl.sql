CREATE TABLE application (

  application_id VARCHAR(100) NOT NULL,

  user_id        VARCHAR(100) NOT NULL,
  code           VARCHAR(255),

  date_created   DATETIME,

  extra          BLOB,

  deleted        BIT(1)  NOT NULL DEFAULT 0,
  optimistic     INT(11) NOT NULL DEFAULT 0,

  UNIQUE (code),
  INDEX (user_id),
  INDEX (deleted),
  PRIMARY KEY (application_id)

);