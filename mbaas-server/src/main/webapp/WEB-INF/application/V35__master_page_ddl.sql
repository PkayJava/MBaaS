CREATE TABLE master_page (

  master_page_id   VARCHAR(100) NOT NULL,
  title            VARCHAR(100) NOT NULL,
  user_id          VARCHAR(100) NOT NULL,
  code             VARCHAR(100) NOT NULL,
  description      VARCHAR(255) NOT NULL,
  javascript       TEXT,
  html             TEXT,
  stage_javascript TEXT,
  stage_html       TEXT,
  modified         BIT(1)       NOT NULL,
  date_created     DATETIME     NOT NULL,
  date_modified    DATETIME     NOT NULL,

  INDEX (modified),
  UNIQUE (code),
  INDEX (title),
  INDEX (description),
  INDEX (user_id),
  FULLTEXT (html),
  FULLTEXT (javascript),
  INDEX (modified),
  FULLTEXT (stage_html),
  FULLTEXT (stage_javascript),
  INDEX (date_created),
  INDEX (date_modified),
  PRIMARY KEY (master_page_id)
);