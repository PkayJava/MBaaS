#MUTABLE
CREATE TABLE `setting` (

  setting_id  VARCHAR(100) NOT NULL,
  name        VARCHAR(100) NOT NULL, #INSTANCE
  `value`     VARCHAR(255) NOT NULL,

  KEY `index__setting__value` (`value`),
  UNIQUE KEY `index__setting__name` (name),
  PRIMARY KEY (setting_id)
);