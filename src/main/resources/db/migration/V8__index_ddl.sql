CREATE TABLE `index` (

  index_id INT(11) AUTO_INCREMENT,

  table_id INT(11)      NOT NULL,
  name     VARCHAR(255) NOT NULL,
  field_id INT(11)      NOT NULL,

  `unique` BIT(1)       NOT NULL,
  type     INT(11),

  UNIQUE (table_id, name, field_id),
  PRIMARY KEY (index_id)

);