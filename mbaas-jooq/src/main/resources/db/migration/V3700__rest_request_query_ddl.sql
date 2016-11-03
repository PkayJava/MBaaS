#MUTABLE
CREATE TABLE rest_request_query (

  rest_request_query_id VARCHAR(100) NOT NULL,
  rest_id               VARCHAR(100) NOT NULL,
  http_query_id         VARCHAR(100) NOT NULL,
  required              BIT(1)       NOT NULL,
  system                BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__rest_request_query__rest_id__http_query_id` (rest_id, http_query_id),
  KEY `index__rest_request_query__system` (system),
  PRIMARY KEY (rest_request_query_id)
);