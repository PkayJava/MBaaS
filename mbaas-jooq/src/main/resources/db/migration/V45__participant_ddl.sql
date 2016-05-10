CREATE TABLE participant (

  participant_id  VARCHAR(100),
  conversation_id VARCHAR(100),
  user_id         VARCHAR(100),
  date_created    DATETIME NOT NULL,

  INDEX (conversation_id),
  INDEX (user_id),
  INDEX (date_created),
  UNIQUE KEY (user_id, conversation_id),
  PRIMARY KEY (participant_id)
);