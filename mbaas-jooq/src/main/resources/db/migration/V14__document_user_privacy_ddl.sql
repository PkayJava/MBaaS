CREATE TABLE document_user_privacy (

  document_user_privacy_id INT(11) AUTO_INCREMENT,

  user_id                  VARCHAR(100) NOT NULL,
  collection_id            VARCHAR(100) NOT NULL,
  document_id              VARCHAR(100) NOT NULL,

  permisson                INT(11)      NOT NULL,

  UNIQUE KEY (user_id, collection_id, document_id),
  PRIMARY KEY (document_user_privacy_id)
);