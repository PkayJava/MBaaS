CREATE TABLE `attribute` (

  attribute_id   VARCHAR(100) NOT NULL,

  collection_id  VARCHAR(100) NOT NULL,
  name           VARCHAR(255) NOT NULL,

  attribute_type VARCHAR(50)  NOT NULL,

  -- SHOWN | HIDED
  visibility     VARCHAR(50)  NOT NULL,

  eav            BIT(1)       NOT NULL,
  system         BIT(1)       NOT NULL,

  extra          INT(11) DEFAULT 0,

  INDEX (name),
  INDEX (eav),
  INDEX (system),
  INDEX (extra),
  INDEX (collection_id),
  UNIQUE KEY (name, collection_id),
  PRIMARY KEY (attribute_id)

);