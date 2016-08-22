CREATE TABLE rest_request_query (

  rest_request_query_id VARCHAR(100) NOT NULL,
  rest_id               VARCHAR(100) NOT NULL,
  http_query_id         VARCHAR(100) NOT NULL,
  required              BIT(1)       NOT NULL,

  UNIQUE KEY (rest_id, http_query_id),
  PRIMARY KEY (rest_request_query_id)
);