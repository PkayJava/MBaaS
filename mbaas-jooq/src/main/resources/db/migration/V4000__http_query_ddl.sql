#MUTABLE
CREATE TABLE http_query (

  http_query_id VARCHAR(100) NOT NULL,
  name          VARCHAR(100) NOT NULL, #INSTANCE
  type          VARCHAR(100) NOT NULL,
  sub_type      VARCHAR(255),
  enum_id       VARCHAR(100),
  description   VARCHAR(100),
  format        VARCHAR(255),
  system        BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__http_query__name` (name),
  KEY `unique__http_query__system` (system),
  PRIMARY KEY (http_query_id)
);