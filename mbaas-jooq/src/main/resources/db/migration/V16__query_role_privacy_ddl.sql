CREATE TABLE query_role_privacy (

  query_role_privacy_id VARCHAR(100) NOT NULL,
  role_id               VARCHAR(100) NOT NULL,
  query_id              VARCHAR(100) NOT NULL,

  permisson             INT(11)      NOT NULL,

  UNIQUE (role_id, query_id),
  PRIMARY KEY (query_role_privacy_id)
);