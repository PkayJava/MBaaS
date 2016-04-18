CREATE TABLE eav_integer (

  eav_integer_id VARCHAR(100) NOT NULL,

  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  value          INT(11),

  UNIQUE (collection_id, attribute_id, document_id),
  INDEX (value),
  PRIMARY KEY (eav_integer_id)

);