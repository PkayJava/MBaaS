CREATE TABLE query_role_privacy (

  role_id   VARCHAR(100) NOT NULL,
  query_id  VARCHAR(100) NOT NULL,

  permisson INT(11)      NOT NULL,

  PRIMARY KEY (role_id, query_id)
);