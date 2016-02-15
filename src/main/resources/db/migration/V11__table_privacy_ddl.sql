CREATE TABLE table_privacy (

  table_privacy_id INT(11) AUTO_INCREMENT,

  user_id             INT(11) NOT NULL,
  table_id            INT(11) NOT NULL,

  permisson           INT(11) NOT NULL,

  UNIQUE KEY (user_id, table_id),
  PRIMARY KEY (table_privacy_id)
);