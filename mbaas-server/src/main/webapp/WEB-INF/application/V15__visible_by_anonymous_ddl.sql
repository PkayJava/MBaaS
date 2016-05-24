CREATE TABLE visible_by_anonymous (

  visible_by_anonymous_id VARCHAR(100) NOT NULL,
  application_user_id     VARCHAR(100) NOT NULL,
  attribute_id            VARCHAR(100) NOT NULL,

  INDEX (application_user_id),
  INDEX (attribute_id),
  UNIQUE KEY (application_user_id, attribute_id),
  PRIMARY KEY (visible_by_anonymous_id)
);