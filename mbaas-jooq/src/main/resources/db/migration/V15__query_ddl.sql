CREATE TABLE query (

  query_id      VARCHAR(100) NOT NULL,
  name          VARCHAR(255) NOT NULL,
  path          VARCHAR(50)  NOT NULL,
  description   VARCHAR(255) NOT NULL,

  script        TEXT         NOT NULL,
  return_type   VARCHAR(50)  NOT NULL,
  date_created  DATETIME     NOT NULL DEFAULT NOW(),
  security      VARCHAR(15)  NOT NULL,

  owner_user_id VARCHAR(100) NOT NULL,

  optimistic    INT(11)      NOT NULL DEFAULT 0,

  UNIQUE KEY (name),
  UNIQUE KEY (path),
  PRIMARY KEY (query_id)

);