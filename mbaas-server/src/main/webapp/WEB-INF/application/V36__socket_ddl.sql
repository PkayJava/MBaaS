CREATE TABLE socket (

  socket_id     VARCHAR(100) NOT NULL,
  user_id       VARCHAR(100),
  session_id    VARCHAR(100) NOT NULL,
  date_created  DATETIME     NOT NULL,
  resource_name VARCHAR(100),
  page_key      INT(11),

  INDEX (user_id),
  INDEX (session_id),
  INDEX (page_key),
  INDEX (resource_name),
  INDEX (date_created),
  PRIMARY KEY (socket_id)
);