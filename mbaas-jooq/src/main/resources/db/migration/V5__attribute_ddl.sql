CREATE TABLE `attribute` (

  attribute_id         VARCHAR(100) NOT NULL,

  collection_id        VARCHAR(100) NOT NULL,
  name                 VARCHAR(255) NOT NULL,

  sql_type             VARCHAR(255) NOT NULL,
  java_type            VARCHAR(255) NOT NULL,
  virtual              BIT(1)       NOT NULL,

  virtual_attribute_id VARCHAR(100) NOT NULL,

  system               BIT(1)       NOT NULL,
  exposed              BIT(1)       NOT NULL,
  nullable             BIT(1)       NOT NULL,
  auto_increment       BIT(1)       NOT NULL,

  INDEX (name),
  INDEX (collection_id),
  UNIQUE KEY (name, collection_id),
  PRIMARY KEY (attribute_id)

);