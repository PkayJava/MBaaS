CREATE TABLE user_privacy (

  user_privacy_id VARCHAR(100) NOT NULL,

  user_id         VARCHAR(100) NOT NULL,
  attribute_id    VARCHAR(100) NOT NULL,

  scope           VARCHAR(50)  NOT NULL,

  INDEX (user_id),
  INDEX (attribute_id),
  INDEX (scope),
  UNIQUE KEY (user_id, attribute_id, scope),
  PRIMARY KEY (user_privacy_id)
);