CREATE TABLE document_role_privacy (

  document_role_privacy_id INT(11) AUTO_INCREMENT,

  role_id                  INT(11) NOT NULL,
  table_id                 INT(11) NOT NULL,
  document_id              INT(11) NOT NULL,

  permisson                INT(11) NOT NULL,

  UNIQUE KEY (role_id, table_id, document_id),
  PRIMARY KEY (document_role_privacy_id)
);