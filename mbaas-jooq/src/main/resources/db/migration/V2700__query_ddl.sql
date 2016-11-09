#MUTABLE
CREATE TABLE query (

  query_id        VARCHAR(100) NOT NULL,
  name            VARCHAR(255) NOT NULL, #INSTANCE
  description     VARCHAR(255) NOT NULL,
  script          TEXT         NOT NULL,
  return_type     VARCHAR(50)  NOT NULL,
  return_sub_type VARCHAR(50),
  date_created    DATETIME     NOT NULL,
  security        VARCHAR(15)  NOT NULL,
  system          BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__query__name` (name),
  KEY `index__query__description` (description),
  KEY `index__query__system` (system),
  KEY `index__query__return_type` (return_type),
  KEY `index__query__return_sub_type` (return_sub_type),
  KEY `index__query__date_created` (date_created),
  KEY `index__query__security` (security),
  PRIMARY KEY (query_id)
);