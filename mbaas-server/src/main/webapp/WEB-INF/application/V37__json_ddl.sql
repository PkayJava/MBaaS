CREATE TABLE json (

  json_id      VARCHAR(100) NOT NULL,
  name         VARCHAR(100) NOT NULL,
  description  VARCHAR(255) NOT NULL,
  content_type VARCHAR(100) NOT NULL,

  INDEX (description),
  UNIQUE (name),
  PRIMARY KEY (json_id)
);