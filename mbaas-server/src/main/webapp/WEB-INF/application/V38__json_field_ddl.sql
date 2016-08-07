CREATE TABLE json_field (

  json_field_id VARCHAR(100) NOT NULL,
  json_id       VARCHAR(100) NOT NULL,
  name          VARCHAR(100) NOT NULL,
  description   VARCHAR(255) NOT NULL,
  type          VARCHAR(255) NOT NULL,
  sub_type      VARCHAR(255),
  map_json_id   VARCHAR(100),
  enum_id       VARCHAR(100),

  UNIQUE KEY (json_id, name),
  PRIMARY KEY (json_field_id)
);