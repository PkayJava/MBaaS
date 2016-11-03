#MUTABLE
CREATE TABLE query_parameter (

  query_parameter_id VARCHAR(100) NOT NULL,
  query_id           VARCHAR(100) NOT NULL,
  name               VARCHAR(255) NOT NULL, #INSTANCE
  type               VARCHAR(50),
  sub_type           VARCHAR(50),
  system             BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__query_parameter__name__query_id` (name, query_id),
  KEY `index__query_parameter__query_id` (query_id),
  KEY `index__query_parameter__system` (system),
  KEY `index__query_parameter__name` (name),
  KEY `index__query_parameter__type` (type),
  KEY `index__query_parameter__sub_type`(sub_type),
  PRIMARY KEY (query_parameter_id)
);