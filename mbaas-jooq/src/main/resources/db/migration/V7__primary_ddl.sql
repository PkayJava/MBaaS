CREATE TABLE `primary` (

  primary_id INT(11) AUTO_INCREMENT,
  table_id   INT(11) NOT NULL,
  field_id   INT(11) NOT NULL,

  UNIQUE KEY (table_id, field_id),
  PRIMARY KEY (primary_id)

);