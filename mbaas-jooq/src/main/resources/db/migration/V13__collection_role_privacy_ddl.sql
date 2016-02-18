CREATE TABLE collection_role_privacy (

  collection_role_privacy_id VARCHAR(100) NOT NULL,

  role_id                    VARCHAR(100) NOT NULL,
  collection_id              VARCHAR(100) NOT NULL,

  permisson                  INT(11)      NOT NULL,

  UNIQUE KEY (role_id, collection_id),
  PRIMARY KEY (collection_role_privacy_id)
);