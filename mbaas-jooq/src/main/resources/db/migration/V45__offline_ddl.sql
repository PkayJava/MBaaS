CREATE TABLE offline (

  offline_id      VARCHAR(100),
  from_user_id    VARCHAR(100),
  to_user_id      VARCHAR(100),
  category        VARCHAR(100),
  language        VARCHAR(50),
  presence_type   VARCHAR(255),
  presence_show   VARCHAR(255),
  presence_status VARCHAR(255),
  message_body    VARCHAR(255),
  date_created    DATETIME NOT NULL,

  INDEX (message_body),
  INDEX (presence_type),
  INDEX (presence_show),
  INDEX (presence_status),
  INDEX (category),
  INDEX (language),
  INDEX (to_user_id),
  INDEX (from_user_id),
  INDEX (date_created),
  PRIMARY KEY (offline_id)

);