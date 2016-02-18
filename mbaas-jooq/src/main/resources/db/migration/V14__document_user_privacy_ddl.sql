CREATE TABLE document_user_privacy (

  user_id       VARCHAR(100) NOT NULL,
  collection_id VARCHAR(100) NOT NULL,
  document_id   VARCHAR(100) NOT NULL,

  permisson     INT(11)      NOT NULL,

  PRIMARY KEY (user_id, collection_id, document_id)
);