CREATE TABLE rest_request_header (

  rest_request_header_id VARCHAR(100) NOT NULL,
  rest_id                VARCHAR(100) NOT NULL,
  http_header_id         VARCHAR(100) NOT NULL,
  required               BIT(1)       NOT NULL,

  UNIQUE KEY (rest_id, http_header_id),
  PRIMARY KEY (rest_request_header_id)
);