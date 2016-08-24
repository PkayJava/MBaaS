CREATE TABLE rest (

  rest_id                   VARCHAR(100) NOT NULL,
  method                    VARCHAR(50)  NOT NULL,
  path                      VARCHAR(255) NOT NULL,
  name                      VARCHAR(255) NOT NULL,
  description               VARCHAR(255) NOT NULL,
  script                    TEXT,
  date_created              DATETIME     NOT NULL DEFAULT NOW(),
  security                  VARCHAR(15)  NOT NULL,
  request_content_type      VARCHAR(100),
  request_body_type         VARCHAR(100),
  request_body_sub_type     VARCHAR(100),
  request_body_map_json_id  VARCHAR(100),
  request_body_enum_id      VARCHAR(100),
  response_content_type     VARCHAR(100),
  response_body_type        VARCHAR(100),
  response_body_sub_type    VARCHAR(100),
  response_body_map_json_id VARCHAR(100),
  response_body_enum_id     VARCHAR(100),

  UNIQUE KEY (path, method),
  INDEX (description),
  INDEX (name),
  FULLTEXT (script),
  INDEX (date_created),
  INDEX (security),
  PRIMARY KEY (rest_id)
);