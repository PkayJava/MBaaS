#MUTABLE
CREATE TABLE rest_response_header (

  rest_response_header_id VARCHAR(100) NOT NULL,
  rest_id                 VARCHAR(100) NOT NULL,
  http_header_id          VARCHAR(100) NOT NULL,
  required                BIT(1)       NOT NULL,
  system                  BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__rest_response_header__rest_id__http_header_id` (rest_id, http_header_id),
  KEY `index__rest_response_header__system` (system),
  PRIMARY KEY (rest_response_header_id)
);