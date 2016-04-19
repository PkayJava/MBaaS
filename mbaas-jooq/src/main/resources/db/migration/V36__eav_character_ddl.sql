CREATE TABLE eav_character (

  eav_character_id VARCHAR(100) NOT NULL,

  collection_id    VARCHAR(100) NOT NULL,
  attribute_id     VARCHAR(100) NOT NULL,
  document_id      VARCHAR(100) NOT NULL,
  attribute_type   VARCHAR(50)  NOT NULL,
  eav_value        VARCHAR(1),

  INDEX (collection_id),
  INDEX (attribute_id),
  INDEX (document_id),
  INDEX (attribute_type),
  INDEX (eav_value),
  UNIQUE (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_character_id)
);