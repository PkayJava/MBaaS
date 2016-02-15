CREATE TABLE document_privacy (

  document_privacy_id INT(11) AUTO_INCREMENT,

  user_id             INT(11) NOT NULL,
  table_id            INT(11) NOT NULL,
  document_id         INT(11) NOT NULL,

  permisson           INT(11) NOT NULL,

  UNIQUE KEY (user_id, table_id, document_id),
  PRIMARY KEY (document_privacy_id)
);