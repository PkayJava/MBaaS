CREATE TABLE friend (

  friend_id        VARCHAR(100) NOT NULL,
  user_id          VARCHAR(100),
  friend_user_id   VARCHAR(100),
  subscription     VARCHAR(30),
  ask_subscription VARCHAR(30),
  date_created     DATETIME     NOT NULL,

  INDEX (user_id),
  INDEX (friend_user_id),
  INDEX (subscription),
  INDEX (ask_subscription),
  INDEX (date_created),
  UNIQUE (user_id, friend_user_id),
  PRIMARY KEY (friend_id)

);