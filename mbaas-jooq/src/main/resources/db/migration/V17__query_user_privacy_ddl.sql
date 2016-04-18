CREATE TABLE query_user_privacy (

  query_user_privacy_id VARCHAR(100) NOT NULL,

  user_id               VARCHAR(100) NOT NULL,
  query_id              VARCHAR(100) NOT NULL,

  permisson             INT(11)      NOT NULL,

  UNIQUE (user_id, query_id),
  PRIMARY KEY (query_user_privacy_id)
);