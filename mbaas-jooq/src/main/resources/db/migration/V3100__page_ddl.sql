#MUTABLE
CREATE TABLE page (

  page_id       VARCHAR(100) NOT NULL,
  layout_id     VARCHAR(100),
  code          VARCHAR(100) NOT NULL,
  title         VARCHAR(100) NOT NULL, #INSTANCE
  path          VARCHAR(255) NOT NULL,
  description   VARCHAR(255) NOT NULL,
  system        BIT(1)       NOT NULL,
  groovy_id     VARCHAR(100),
  html          TEXT,
  html_crc32    VARCHAR(100),
  modified      BIT(1)       NOT NULL,
  date_created  DATETIME     NOT NULL,
  date_modified DATETIME     NOT NULL,
  cms_page      BIT(1)       NOT NULL DEFAULT TRUE,

  UNIQUE KEY `unique__page__code` (code),
  UNIQUE KEY `unique__page__path` (path),
  KEY `index__page__title` (title),
  KEY `index__page__html_crc32` (html_crc32),
  KEY `index__page__groovy_id` (groovy_id),
  KEY `index__page__cms_page` (cms_page),
  KEY `index__page__layout_id` (layout_id),
  KEY `index__page__description` (description),
  KEY `index__page__modified` (modified),
  KEY `index__page__date_created` (date_created),
  KEY `index__page__date_modified` (date_modified),
  KEY `index__page__system` (system),
  PRIMARY KEY (page_id)
);