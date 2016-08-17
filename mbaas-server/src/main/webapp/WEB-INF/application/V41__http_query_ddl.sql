CREATE TABLE request_query (

  http_query_id VARCHAR(100) NOT NULL,
  name          VARCHAR(100) NOT NULL,
  type          VARCHAR(100),
  enum_id       VARCHAR(100),
  description   VARCHAR(100),

  PRIMARY KEY (http_query_id)
);