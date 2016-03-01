CREATE TABLE setting (

  setting_id VARCHAR(100) NOT NULL,

  `label`    VARCHAR(255) NOT NULL,
  `key`      VARCHAR(255) NOT NULL,
  `value`    VARCHAR(255) NOT NULL,
  java_type  VARCHAR(255) NOT NULL,

  UNIQUE KEY (`key`),
  PRIMARY KEY (setting_id)

);