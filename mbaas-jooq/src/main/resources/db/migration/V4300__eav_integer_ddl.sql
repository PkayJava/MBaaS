#MUTABLE
CREATE TABLE eav_integer (

  eav_integer_id VARCHAR(100) NOT NULL,
  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  eav_value      INT(11),

  KEY `index__eav_integer__collection_id` (collection_id),
  KEY `index__eav_integer__attribute_id` (attribute_id),
  KEY `index__eav_integer__document_id` (document_id),
  KEY `index__eav_integer__attribute_type` (attribute_type),
  KEY `index__eav_integer__eav_value` (eav_value),
  UNIQUE KEY `unique__eav_integer__collection_id__attribute_id__document_id` (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_integer_id)
);