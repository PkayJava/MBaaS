CREATE TABLE query (

  query_id      VARCHAR(100) NOT NULL,
  name          VARCHAR(255) NOT NULL,

  owner_user_id VARCHAR(100) NOT NULL,

  deleted       BIT(1)       NOT NULL DEFAULT 0,
  optimistic    INT(11)      NOT NULL DEFAULT 0,

  UNIQUE KEY (name),
  PRIMARY KEY (query_id)

);