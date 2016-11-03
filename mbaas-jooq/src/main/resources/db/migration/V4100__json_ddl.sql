#MUTABLE
CREATE TABLE json (

  json_id      VARCHAR(100) NOT NULL,
  name         VARCHAR(100) NOT NULL, #INSTANCE
  description  VARCHAR(255) NOT NULL,
  content_type VARCHAR(100) NOT NULL,
  system       BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__json__description` (description),
  KEY `index__json__system` (system),
  UNIQUE KEY `unique__json__name` (name),
  PRIMARY KEY (json_id)
);