CREATE TABLE application (

  application_id INT(11) AUTO_INCREMENT,

  user_id        INT(11),
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