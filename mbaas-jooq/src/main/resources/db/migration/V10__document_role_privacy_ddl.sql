CREATE TABLE document_role_privacy (

  document_role_privacy_id VARCHAR(100) NOT NULL,

  role_id                  VARCHAR(100) NOT NULL,
  collection_id            VARCHAR(100) NOT NULL,
  document_id              VARCHAR(100) NOT NULL,

  permisson                INT(11)      NOT NULL,

  UNIQUE (role_id, collection_id, document_id),
  PRIMARY KEY (document_role_privacy_id)
);
