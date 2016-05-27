CREATE TABLE page (

  page_id     VARCHAR(100) NOT NULL,
  title       VARCHAR(100) NOT NULL,
  description VARCHAR(255) NOT NULL,
  javascript  TEXT,

  INDEX (title),
  INDEX (description),
  FULLTEXT (javascript),
  PRIMARY KEY (page_id)
);