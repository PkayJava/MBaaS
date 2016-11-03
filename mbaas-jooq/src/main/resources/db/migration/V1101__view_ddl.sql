#MUTABLE
CREATE TABLE `view` (

  view_id VARCHAR(100) NOT NULL,
  name    VARCHAR(255) NOT NULL, #INSTANCE
  script  TEXT         NOT NULL,
  system  BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__view__name` (name),
  KEY `index__view__system` (system),
  PRIMARY KEY (view_id)
);