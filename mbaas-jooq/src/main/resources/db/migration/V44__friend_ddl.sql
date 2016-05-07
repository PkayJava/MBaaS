CREATE TABLE friend (

  friend_id      VARCHAR(100),
  user_id        VARCHAR(100),
  friend_user_id VARCHAR(100),
  subscription   VARCHAR(10),
  date_created   DATETIME NOT NULL,

  INDEX (user_id),
  INDEX (friend_user_id),
  INDEX (subscription),
  INDEX (date_created),
  PRIMARY KEY (friend_id)
);