CREATE TABLE page (

  page_id       VARCHAR(100) NOT NULL,
  title         VARCHAR(100) NOT NULL,
  user_id       VARCHAR(100) NOT NULL,
  menu_id       VARCHAR(100) NOT NULL,
  description   VARCHAR(255) NOT NULL,
  security      VARCHAR(50)  NOT NULL,
  javascript    TEXT,
  html          TEXT,
  date_created  DATETIME     NOT NULL,
  date_modified DATETIME     NOT NULL,

  INDEX (title),
  INDEX (description),
  INDEX (user_id),
  INDEX (menu_id),
  INDEX (security),
  FULLTEXT (javascript),
  INDEX (date_created),
  INDEX (date_modified),
  PRIMARY KEY (page_id)
);