CREATE TABLE request_query (

  request_query_id VARCHAR(100) NOT NULL,
  name             VARCHAR(100),
  type             VARCHAR(100),
  enum_id          VARCHAR(100),

  PRIMARY KEY (request_query_id)
);