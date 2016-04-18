CREATE TABLE eav_date_time (

  eav_date_time_id VARCHAR(100) NOT NULL,

  collection_id   VARCHAR(100) NOT NULL,
  attribute_id    VARCHAR(100) NOT NULL,
  document_id     VARCHAR(100) NOT NULL,
  attribute_type  VARCHAR(50)  NOT NULL,
  value           DATETIME,

  UNIQUE (collection_id, attribute_id, document_id),
  INDEX (value),
  PRIMARY KEY (eav_date_time_id)

);