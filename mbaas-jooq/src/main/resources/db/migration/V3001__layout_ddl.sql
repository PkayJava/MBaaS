#MUTABLE
CREATE TABLE layout (

  layout_id     VARCHAR(100) NOT NULL,
  title         VARCHAR(100) NOT NULL, #INSTANCE
  description   VARCHAR(255) NOT NULL,
  system        BIT(1)       NOT NULL,
  groovy_id     VARCHAR(100),
  html          TEXT,
  html_crc32    VARCHAR(100),
  modified      BIT(1)       NOT NULL,
  date_created  DATETIME     NOT NULL,
  date_modified DATETIME     NOT NULL,
  cms_layout    BIT(1)       NOT NULL DEFAULT TRUE,

  UNIQUE KEY `unique__layout__title` (title),
  UNIQUE KEY `unique__layout__groovy_id` (groovy_id),
  KEY `index__layout__html_crc32` (html_crc32),
  KEY `index__layout__modified` (modified),
  KEY `index__layout__cms_layout` (cms_layout),
  KEY `index__layout__description` (description),
  KEY `index__layout__date_created` (date_created),
  KEY `index__layout__date_modified` (date_modified),
  KEY `index__layout__system` (system),
  PRIMARY KEY (layout_id)
);