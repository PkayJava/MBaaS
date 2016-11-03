#MUTABLE
CREATE TABLE restore (

  restore_id   VARCHAR(100) NOT NULL,
  fields       TEXT,
  table_name   VARCHAR(100),
  date_created DATETIME     NOT NULL,

  FULLTEXT KEY `fulltext__restore__fields` (fields),
  KEY `index__restore__table_name` (table_name),
  KEY `index__restore__date_created` (date_created),
  PRIMARY KEY (restore_id)
);