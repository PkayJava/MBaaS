CREATE TABLE visible_by_friend (

  visible_by_friend_id VARCHAR(100) NOT NULL,
  user_id              VARCHAR(100) NOT NULL,
  attribute_id         VARCHAR(100) NOT NULL,

  INDEX (user_id),
  INDEX (attribute_id),
  UNIQUE KEY (user_id, attribute_id),
  PRIMARY KEY (visible_by_friend_id)
);