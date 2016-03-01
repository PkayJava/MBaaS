CREATE TABLE query_user_privacy (

  user_id   VARCHAR(100) NOT NULL,
  query_id  VARCHAR(100) NOT NULL,

  permisson INT(11)      NOT NULL,

  PRIMARY KEY (user_id, query_id)
);