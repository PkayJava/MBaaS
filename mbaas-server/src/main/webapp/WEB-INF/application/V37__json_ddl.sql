CREATE TABLE json (

  json_id     VARCHAR(100) NOT NULL,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(255) NOT NULL,

  INDEX (description),
  UNIQUE KEY (name),
  PRIMARY KEY (json_id)
);