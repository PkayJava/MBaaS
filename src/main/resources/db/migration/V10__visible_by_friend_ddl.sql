CREATE TABLE visible_by_friend (

  visible_by_friend_id INT(11)          AUTO_INCREMENT,
  user_id                INT(11) NOT NULL,

  extra                  BLOB,

  deleted                BIT(1)  NOT NULL DEFAULT 0,
  optimistic             INT(11) NOT NULL DEFAULT 0,

  UNIQUE KEY (user_id),
  INDEX (deleted),
  PRIMARY KEY (visible_by_friend_id)
);