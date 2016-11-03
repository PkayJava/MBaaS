#MUTABLE
CREATE TABLE block (

  block_id         VARCHAR(100) NOT NULL,
  code             VARCHAR(100) NOT NULL,
  title            VARCHAR(100) NOT NULL, #INSTANCE
  description      VARCHAR(255) NOT NULL,
  javascript       TEXT,
  html             TEXT,
  stage_javascript TEXT,
  stage_html       TEXT,
  modified         BIT(1)       NOT NULL,
  date_created     DATETIME     NOT NULL,
  date_modified    DATETIME     NOT NULL,
  system           BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__block__code` (code),
  KEY `index__block__title` (title),
  KEY `index__block__system` (system),
  KEY `index__block__description` (description),
  KEY `index__block__modified` (modified),
  KEY `index__block__date_created` (date_created),
  KEY `index__block__date_modified` (date_modified),
  PRIMARY KEY (block_id)
);