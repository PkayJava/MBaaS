#MUTABLE
CREATE TABLE eav_date (

  eav_date_id    VARCHAR(100) NOT NULL,
  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  eav_value      DATE,

  KEY `index__eav_date__collection_id` (collection_id),
  KEY `index__eav_date__attribute_id` (attribute_id),
  KEY `index__eav_date__document_id` (document_id),
  KEY `index__eav_date__attribute_type` (attribute_type),
  KEY `index__eav_date__eav_value` (eav_value),
  UNIQUE KEY `unique__eav_date__collection_id__attribute_id__document_id` (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_date_id)
);