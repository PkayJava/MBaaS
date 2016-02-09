CREATE TABLE setting (

  setting_id INT(11) AUTO_INCREMENT,

  `label`    VARCHAR(255) NOT NULL,
  `key`      VARCHAR(255) NOT NULL,
  `value`    VARCHAR(255) NOT NULL,
  java_type  VARCHAR(255) NOT NULL,

  UNIQUE KEY (`key`),
  PRIMARY KEY (setting_id)

);