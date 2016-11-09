#MUTABLE
CREATE TABLE file (

  file_id      VARCHAR(100) NOT NULL,
  name         VARCHAR(255),
  label        VARCHAR(255),
  path         VARCHAR(255) NOT NULL,
  mime         VARCHAR(100),
  extension    VARCHAR(10),
  `length`     INT(11)      NOT NULL,
  date_created DATETIME     NOT NULL,
  system       BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__file__system` (system),
  KEY `index__file__name` (name),
  KEY `index__file__label` (label),
  KEY `index__file__path` (path),
  KEY `index__file__mime` (mime),
  KEY `index__file__extension` (extension),
  KEY `index__file__length` (`length`),
  KEY `index__file__date_created` (date_created),
  PRIMARY KEY (file_id)
);
