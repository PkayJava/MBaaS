CREATE TABLE table_role_privacy (

  table_role_privacy_id INT(11) AUTO_INCREMENT,

  role_id               INT(11) NOT NULL,
  table_id              INT(11) NOT NULL,

  permisson             INT(11) NOT NULL,

  UNIQUE KEY (role_id, table_id),
  PRIMARY KEY (table_role_privacy_id)
);