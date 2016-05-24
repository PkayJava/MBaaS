CREATE TABLE query_parameter (

  query_parameter_id VARCHAR(100) NOT NULL,
  query_id           VARCHAR(100) NOT NULL,
  application_code   VARCHAR(100) NOT NULL,
  name               VARCHAR(255) NOT NULL,
  type               VARCHAR(50),
  sub_type           VARCHAR(50),

  UNIQUE KEY (name, query_id),
  INDEX (query_id),
  INDEX (application_code),
  INDEX (name),
  INDEX (type),
  INDEX (sub_type),
  PRIMARY KEY (query_parameter_id)
);