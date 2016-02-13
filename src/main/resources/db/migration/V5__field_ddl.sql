CREATE TABLE `field` (

  field_id         INT(11) AUTO_INCREMENT,

  table_id         INT(11)      NOT NULL,
  name             VARCHAR(255) NOT NULL,

  sql_type         VARCHAR(255) NOT NULL,
  java_type        VARCHAR(255) NOT NULL,
  virtual          BIT(1)       NOT NULL,
  virtual_field_id INT(11),
  system           BIT(1)       NOT NULL,
  exposed          BIT(1)       NOT NULL,
  nullable         BIT(1)       NOT NULL,
  auto_increment   BIT(1)       NOT NULL,

  INDEX (NAME),
  INDEX (table_id),
  UNIQUE KEY (NAME, table_id),
  PRIMARY KEY (field_id)

);