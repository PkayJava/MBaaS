#MUTABLE
CREATE TABLE eav_text (

  eav_text_id    VARCHAR(100) NOT NULL,
  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  eav_value      TEXT,

  KEY `index__eav_text__collection_id` (collection_id),
  KEY `index__eav_text__attribute_id` (attribute_id),
  KEY `index__eav_text__document_id` (document_id),
  KEY `index__eav_text__attribute_type` (attribute_type),
  FULLTEXT KEY `fulltext__eav_text__eav_value` (eav_value),
  UNIQUE KEY `unique__eav_text__collection_id__attribute_id__document_id` (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_text_id)
);