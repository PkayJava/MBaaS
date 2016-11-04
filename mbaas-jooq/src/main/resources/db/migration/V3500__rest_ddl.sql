#MUTABLE
CREATE TABLE rest (

  rest_id     VARCHAR(100) NOT NULL,
  method      VARCHAR(10)  NOT NULL,
  path        VARCHAR(255) NOT NULL,
  name        VARCHAR(255) NOT NULL, #INSTANCE
  description VARCHAR(255) NOT NULL,
  groovy_id   VARCHAR(100),
  security    VARCHAR(15)  NOT NULL,
  system      BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__rest__path__method` (path, method),
  KEY `index__rest__description` (description),
  KEY `index__rest__system` (system),
  KEY `index__rest__groovy_id` (groovy_id),
  KEY `index__rest__name` (name),
  KEY `index__rest__security` (security),
  PRIMARY KEY (rest_id)
);