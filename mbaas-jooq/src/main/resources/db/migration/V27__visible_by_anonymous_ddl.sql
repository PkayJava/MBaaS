CREATE TABLE visible_by_anonymous (

  visible_by_anonymous_id VARCHAR(100) NOT NULL,

  user_id                 VARCHAR(100) NOT NULL,
  attribute_id            VARCHAR(100) NOT NULL,

  UNIQUE KEY (user_id, attribute_id),
  PRIMARY KEY (visible_by_anonymous_id)
);