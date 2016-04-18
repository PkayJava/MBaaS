CREATE TABLE eav_text (

  eav_text_id    VARCHAR(100) NOT NULL,

  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  value          TEXT,

  UNIQUE (collection_id, attribute_id, document_id),
  FULLTEXT (value),
  PRIMARY KEY (eav_text_id)

);