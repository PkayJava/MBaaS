CREATE TABLE restore (

  restore_id   VARCHAR(100) NOT NULL,
  fields       TEXT,
  table_name   VARCHAR(100),
  date_created DATETIME     NOT NULL,

  FULLTEXT (fields),
  INDEX (table_name),
  INDEX (date_created),
  PRIMARY KEY (restore_id)

);