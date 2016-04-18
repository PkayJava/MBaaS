CREATE TABLE eav_boolean (

  eav_boolean_id VARCHAR(100) NOT NULL,

  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  value          BIT(1),

  UNIQUE (collection_id, attribute_id, document_id),
  INDEX (value),
  PRIMARY KEY (eav_boolean_id)

);