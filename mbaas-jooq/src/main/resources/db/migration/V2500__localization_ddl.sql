#MUTABLE
CREATE TABLE localization (

  localization_id VARCHAR(100) NOT NULL,
  `key`           VARCHAR(100) NOT NULL,
  `page`          VARCHAR(255),
  `language`      VARCHAR(10),
  `label`         VARCHAR(255) NOT NULL,
  system          BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__localization__system` (`system`),
  KEY `index__localization__key` (`key`),
  KEY `index__localization__page` (`page`),
  KEY `index__localization__language` (`language`),
  KEY `index__localization__label` (`label`),
  UNIQUE KEY `index__localization__key__page__language` (`key`, `page`, `language`),
  PRIMARY KEY (localization_id)
);