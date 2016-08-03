CREATE TABLE json (

  json_id VARCHAR(100) NOT NULL,
  name    VARCHAR(100),

  INDEX (name),
  PRIMARY KEY (json_id)
);