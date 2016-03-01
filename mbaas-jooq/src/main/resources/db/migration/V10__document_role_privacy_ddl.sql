CREATE TABLE document_role_privacy (

  role_id       VARCHAR(100) NOT NULL,
  collection_id VARCHAR(100) NOT NULL,
  document_id   VARCHAR(100) NOT NULL,

  permisson     INT(11)      NOT NULL,

  PRIMARY KEY (role_id, collection_id, document_id)
);
