CREATE TABLE query_role_privacy (

  query_role_privacy_id VARCHAR(100) NOT NULL,
  role_id               VARCHAR(100) NOT NULL,
  query_id              VARCHAR(100) NOT NULL,
  application_id        VARCHAR(100) NOT NULL,
  permisson             INT(11)      NOT NULL,

  INDEX (role_id),
  INDEX (query_id),
  INDEX (application_id),
  INDEX (permisson),
  UNIQUE (role_id, query_id),
  PRIMARY KEY (query_role_privacy_id)
);