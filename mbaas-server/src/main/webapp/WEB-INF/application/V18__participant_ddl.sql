CREATE TABLE participant (

  participant_id      VARCHAR(100) NOT NULL,
  conversation_id     VARCHAR(100),
  application_user_id VARCHAR(100),
  date_created        DATETIME     NOT NULL,

  INDEX (conversation_id),
  INDEX (application_user_id),
  INDEX (date_created),
  UNIQUE KEY (application_user_id, conversation_id),
  PRIMARY KEY (participant_id)
);