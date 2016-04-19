CREATE TABLE query_parameter (

  query_parameter_id VARCHAR(100) NOT NULL,
  query_id           VARCHAR(100) NOT NULL,
  name               VARCHAR(255) NOT NULL,
  type               VARCHAR(50),
  sub_type           VARCHAR(50),

  INDEX (query_id),
  INDEX (name),
  INDEX (type),
  INDEX (sub_type),
  UNIQUE KEY (name, query_id),
  PRIMARY KEY (query_parameter_id)
);