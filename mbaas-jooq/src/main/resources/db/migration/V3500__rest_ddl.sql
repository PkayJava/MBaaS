#MUTABLE
CREATE TABLE rest (

  rest_id       VARCHAR(100) NOT NULL,
  method        VARCHAR(10)  NOT NULL,
  path          VARCHAR(255) NOT NULL,
  path_variable VARCHAR(255) NOT NULL,
  name          VARCHAR(255) NOT NULL, #INSTANCE
  description   VARCHAR(255) NOT NULL,
  segment       INT(11)      NOT NULL,
  groovy_id     VARCHAR(100),
  security      VARCHAR(15)  NOT NULL,
  system        BIT(1)       NOT NULL DEFAULT 0,
  modified      BIT(1)       NOT NULL,
  date_created  DATETIME     NOT NULL,
  date_modified DATETIME     NOT NULL,

  UNIQUE KEY `unique__rest__path__method` (path, method),
  UNIQUE KEY `unique__rest__path_variable__method` (path_variable, method),
  KEY `index__rest__description` (description),
  KEY `index__rest__segment` (segment),
  KEY `index__rest__system` (system),
  KEY `index__rest__groovy_id` (groovy_id),
  KEY `index__rest__name` (name),
  KEY `index__rest__security` (security),
  KEY `index__rest__modified` (modified),
  KEY `index__rest__date_created` (date_created),
  KEY `index__rest__date_modified` (date_modified),
  PRIMARY KEY (rest_id)
);