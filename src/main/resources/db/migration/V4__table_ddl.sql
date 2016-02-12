CREATE TABLE `table` (

  table_id INT(11) AUTO_INCREMENT,
  name     VARCHAR(255) NOT NULL,
  system   BIT(1)       NOT NULL,

  UNIQUE KEY (name),
  PRIMARY KEY (table_id)

);