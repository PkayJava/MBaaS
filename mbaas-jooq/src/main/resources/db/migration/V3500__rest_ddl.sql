#MUTABLE
CREATE TABLE rest (

  rest_id                   VARCHAR(100) NOT NULL,
  method                    VARCHAR(50)  NOT NULL,
  path                      VARCHAR(255) NOT NULL,
  name                      VARCHAR(255) NOT NULL, #INSTANCE
  description               VARCHAR(255) NOT NULL,
  script                    TEXT,
  stage_script              TEXT,
  modified                  BIT(1)       NOT NULL,
  date_created              DATETIME     NOT NULL DEFAULT NOW(),
  security                  VARCHAR(15)  NOT NULL,
  request_content_type      VARCHAR(100),
  request_body_required     BIT(1)       NOT NULL,
  request_body_type         VARCHAR(100),
  request_body_sub_type     VARCHAR(100),
  request_body_map_json_id  VARCHAR(100),
  request_body_enum_id      VARCHAR(100),
  response_content_type     VARCHAR(100),
  response_body_required    BIT(1)       NOT NULL,
  response_body_type        VARCHAR(100),
  response_body_sub_type    VARCHAR(100),
  response_body_map_json_id VARCHAR(100),
  response_body_enum_id     VARCHAR(100),
  system                    BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__rest__path__method` (path, method),
  FULLTEXT KEY `fulltext__rest__script` (script),
  KEY `index__rest__description` (description),
  KEY `index__rest__system` (system),
  KEY `index__rest__name` (name),
  KEY `index__rest__date_created` (date_created),
  KEY `index__rest__security` (security),
  PRIMARY KEY (rest_id)
);