CREATE TABLE rest_response_header (

  rest_response_header_id VARCHAR(100) NOT NULL,
  rest_id                 VARCHAR(100) NOT NULL,
  http_header_id          VARCHAR(100) NOT NULL,
  required                BIT(1)       NOT NULL,

  UNIQUE (rest_id, http_header_id),
  PRIMARY KEY (rest_response_header_id)
);