CREATE TABLE query_param (

  query_param_id VARCHAR(100) NOT NULL,
  query_id       VARCHAR(100) NOT NULL,
  name           VARCHAR(255) NOT NULL,
  type           VARCHAR(50),

  optimistic     INT(11)      NOT NULL DEFAULT 0,

  UNIQUE KEY (name, query_id),
  PRIMARY KEY (query_param_id)

);