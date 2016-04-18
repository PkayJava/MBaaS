CREATE TABLE eav_varchar (

  eav_varchar_id VARCHAR(100) NOT NULL,

  collection_id  VARCHAR(100) NOT NULL,
  attribute_id   VARCHAR(100) NOT NULL,
  document_id    VARCHAR(100) NOT NULL,
  attribute_type VARCHAR(50)  NOT NULL,
  value          VARCHAR(255),

  UNIQUE (collection_id, attribute_id, document_id),
  FULLTEXT (value),
  PRIMARY KEY (eav_varchar_id)

);