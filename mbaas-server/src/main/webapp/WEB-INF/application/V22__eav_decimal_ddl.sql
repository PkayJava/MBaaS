CREATE TABLE eav_decimal (

  eav_decimal_id   VARCHAR(100) NOT NULL,
  application_code VARCHAR(100) NOT NULL,
  collection_id    VARCHAR(100) NOT NULL,
  attribute_id     VARCHAR(100) NOT NULL,
  document_id      VARCHAR(100) NOT NULL,
  attribute_type   VARCHAR(50)  NOT NULL,
  eav_value        DECIMAL(15, 4),

  INDEX (collection_id),
  INDEX (application_code),
  INDEX (attribute_id),
  INDEX (document_id),
  INDEX (attribute_type),
  INDEX (eav_value),
  UNIQUE (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_decimal_id)
);