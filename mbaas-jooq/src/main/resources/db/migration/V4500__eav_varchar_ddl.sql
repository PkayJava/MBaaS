#MUTABLE
CREATE TABLE eav_varchar (

  eav_varchar_id VARCHAR(100) NOT NULL,
  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  eav_value      VARCHAR(255),

  KEY `index__eav_varchar__collection_id` (collection_id),
  KEY `index__eav_varchar__attribute_id` (attribute_id),
  KEY `index__eav_varchar__document_id` (document_id),
  KEY `index__eav_varchar__attribute_type` (attribute_type),
  FULLTEXT KEY `fulltext__eav_varchar__eav_value` (eav_value),
  UNIQUE KEY `unique__eav_varchar__collection_id__attribute_id__document_id` (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_varchar_id)
);