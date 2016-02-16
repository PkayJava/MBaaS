CREATE TABLE `table` (

  table_id      INT(11)               AUTO_INCREMENT,
  name          VARCHAR(255) NOT NULL,
  locked        BIT(1)       NOT NULL DEFAULT 0,
  system        BIT(1)       NOT NULL,
  owner_user_id INT(11)      NOT NULL,

  UNIQUE KEY (name),
  PRIMARY KEY (table_id)

);