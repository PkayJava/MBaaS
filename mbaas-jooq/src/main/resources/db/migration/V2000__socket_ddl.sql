#MUTABLE
CREATE TABLE socket (

  socket_id     VARCHAR(100) NOT NULL,
  user_id       VARCHAR(100),
  session_id    VARCHAR(100) NOT NULL,
  date_created  DATETIME     NOT NULL,
  resource_name VARCHAR(100),
  page_key      INT(11),
  system        BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__socket__system` (system),
  KEY `index__socket__user_id` (user_id),
  KEY `index__socket__session_id` (session_id),
  KEY `index__socket__page_key` (page_key),
  KEY `index__socket__resource_name` (resource_name),
  KEY `index__socket__date_created` (date_created),
  PRIMARY KEY (socket_id)
);