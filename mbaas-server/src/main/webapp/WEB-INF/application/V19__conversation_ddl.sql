CREATE TABLE conversation (

  conversation_id VARCHAR(100) NOT NULL,
  name            VARCHAR(100),
  date_created    DATETIME     NOT NULL,

  INDEX (name),
  INDEX (date_created),
  PRIMARY KEY (conversation_id)
);