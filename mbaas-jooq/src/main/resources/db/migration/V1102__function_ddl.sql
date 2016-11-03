#MUTABLE
CREATE TABLE `function` (

  function_id VARCHAR(100) NOT NULL,
  name        VARCHAR(255) NOT NULL, #INSTANCE
  script      TEXT         NOT NULL,
  system      BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__function__name` (name),
  KEY `index__function__system` (system),
  PRIMARY KEY (function_id)
);