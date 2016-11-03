#MUTABLE
CREATE TABLE `primary_attribute` (

  primary_attribute_id VARCHAR(100) NOT NULL,
  collection_id        VARCHAR(100) NOT NULL,
  attribute_id         VARCHAR(100) NOT NULL,
  system               BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__primary_attribute__system` (system),
  UNIQUE KEY `unique__primary_attribute__collection_id__attribute_id` (collection_id, attribute_id),
  PRIMARY KEY (primary_attribute_id)
);