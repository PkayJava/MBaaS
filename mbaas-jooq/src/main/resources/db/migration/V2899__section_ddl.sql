#MUTABLE
CREATE TABLE `section` (

  section_id VARCHAR(100) NOT NULL,
  title      VARCHAR(100) NOT NULL, #INSTANCE
  system     BIT(1)       NOT NULL DEFAULT 0,
  `order`    INT(11)      NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__section__title` (title),
  KEY `index__section__order` (`order`),
  KEY `index__section__system` (system),
  PRIMARY KEY (section_id)
);