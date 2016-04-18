CREATE TABLE collection_user_privacy (

  collection_user_privacy_id VARCHAR(100) NOT NULL,

  user_id                    VARCHAR(100) NOT NULL,
  collection_id              VARCHAR(100) NOT NULL,

  permisson                  INT(11)      NOT NULL,

  UNIQUE (user_id, collection_id),
  PRIMARY KEY (collection_user_privacy_id)
);