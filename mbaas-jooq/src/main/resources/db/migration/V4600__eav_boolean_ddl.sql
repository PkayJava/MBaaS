#MUTABLE
CREATE TABLE eav_boolean (

  eav_boolean_id VARCHAR(100) NOT NULL,
  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  eav_value      BIT(1),

  KEY `index__eav_boolean__collection_id` (collection_id),
  KEY `index__eav_boolean__attribute_id` (attribute_id),
  KEY `index__eav_boolean__document_id` (document_id),
  KEY `index__eav_boolean__attribute_type` (attribute_type),
  KEY `index__eav_boolean__eav_value` (eav_value),
  UNIQUE KEY `unique__eav_boolean__collection_id__attribute_id__document_id` (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_boolean_id)
);