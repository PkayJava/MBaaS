CREATE TABLE file (

  file_id          VARCHAR(100) NOT NULL,
  application_code VARCHAR(100) NOT NULL,
  name             VARCHAR(255),
  label            VARCHAR(255),
  path             VARCHAR(255) NOT NULL,
  mime             VARCHAR(100),
  extension        VARCHAR(10),
  `length`         INT(11)      NOT NULL,
  date_created     DATETIME     NOT NULL DEFAULT NOW(),
  user_id          VARCHAR(100),
  client_id        VARCHAR(100),

  INDEX (application_code),
  INDEX (name),
  INDEX (label),
  INDEX (path),
  INDEX (mime),
  INDEX (extension),
  INDEX (`length`),
  INDEX (date_created),
  INDEX (user_id),
  INDEX (client_id),
  PRIMARY KEY (file_id)
);