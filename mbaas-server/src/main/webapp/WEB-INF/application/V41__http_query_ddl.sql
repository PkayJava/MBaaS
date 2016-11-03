CREATE TABLE http_query (

  http_query_id VARCHAR(100) NOT NULL,
  name          VARCHAR(100),
  -- Long, Double, String, Date, Time, DateTime, Boolean, Enum, Array
  type          VARCHAR(100) NOT NULL,
  sub_type      VARCHAR(255),
  -- Long, Double, String, Date, Time, DateTime, Boolean
  enum_id       VARCHAR(100),
  description   VARCHAR(100),
  format        VARCHAR(255),

  UNIQUE (name),
  PRIMARY KEY (http_query_id)
);