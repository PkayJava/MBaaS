CREATE TABLE eav_text (

  eav_text_id    VARCHAR(100) NOT NULL,
  application_id VARCHAR(100) NOT NULL,
  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  eav_value      TEXT,

  INDEX (collection_id),
  INDEX (application_id),
  INDEX (attribute_id),
  INDEX (document_id),
  INDEX (attribute_type),
  FULLTEXT (eav_value),
  UNIQUE (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_text_id)
);