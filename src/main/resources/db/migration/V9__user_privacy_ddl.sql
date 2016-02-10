CREATE TABLE user_privacy (

  user_privacy_id INT(11) AUTO_INCREMENT,
  user_id         INT(11)     NOT NULL,
  field_id        INT(11)     NOT NULL,
  scope           VARCHAR(50) NOT NULL,

  UNIQUE KEY (user_id, field_id, scope),
  PRIMARY KEY (user_privacy_id)
);