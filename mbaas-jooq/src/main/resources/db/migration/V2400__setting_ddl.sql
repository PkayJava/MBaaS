#MUTABLE
CREATE TABLE `setting` (

  setting_id  VARCHAR(100) NOT NULL,
  name        VARCHAR(100) NOT NULL, #INSTANCE
  description VARCHAR(255) NOT NULL,
  `value`     VARCHAR(255) NOT NULL,
  system      BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__setting__description` (description),
  KEY `index__setting__value` (`value`),
  KEY `index__setting__system` (system),
  UNIQUE KEY `index__setting__name` (name),
  PRIMARY KEY (setting_id)
);