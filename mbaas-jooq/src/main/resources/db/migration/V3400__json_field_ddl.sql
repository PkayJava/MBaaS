#MUTABLE
CREATE TABLE json_field (

  json_field_id VARCHAR(100) NOT NULL,
  json_id       VARCHAR(100) NOT NULL,
  name          VARCHAR(100) NOT NULL, #INSTANCE
  description   VARCHAR(255) NOT NULL,
  required      BIT(1)       NOT NULL,
  type          VARCHAR(255) NOT NULL,
  sub_type      VARCHAR(255),
  map_json_id   VARCHAR(100),
  enum_id       VARCHAR(100),
  system        BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__json_field__json_id__name` (json_id, name),
  KEY `index__json_field__system` (system),
  PRIMARY KEY (json_field_id)
);