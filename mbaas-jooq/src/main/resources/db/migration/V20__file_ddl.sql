CREATE TABLE file (

  file_id       VARCHAR(100) NOT NULL,

  `name`        VARCHAR(255),
  `label`       VARCHAR(255),
  `path`        VARCHAR(255) NOT NULL,
  `mime`        VARCHAR(30),
  `extension`   VARCHAR(10),
  `length`      INT(11)      NOT NULL,

  extra         BLOB,

  owner_user_id VARCHAR(100) NOT NULL,

  optimistic    INT(11)      NOT NULL DEFAULT 0,

  INDEX (`name`),
  INDEX (`label`),
  INDEX (`owner_user_id`),
  INDEX (`path`),
  INDEX (`mime`),
  INDEX (`extension`),
  INDEX (`length`),
  PRIMARY KEY (file_id)

);