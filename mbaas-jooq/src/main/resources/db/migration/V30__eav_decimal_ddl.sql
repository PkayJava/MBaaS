CREATE TABLE eav_decimal (

  eav_decimal_id VARCHAR(100) NOT NULL,

  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  value          DECIMAL(15, 4),

  UNIQUE (collection_id, attribute_id, document_id),
  INDEX (value),
  PRIMARY KEY (eav_decimal_id)

);