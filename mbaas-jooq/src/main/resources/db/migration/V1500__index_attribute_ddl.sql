#MUTABLE
CREATE TABLE `index_attribute` (

  index_attribute_id VARCHAR(100) NOT NULL,
  collection_id      VARCHAR(100) NOT NULL,
  attribute_id       VARCHAR(100) NOT NULL,
  name               VARCHAR(100) NOT NULL,
  type               VARCHAR(12)  NOT NULL,
  system             BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__index_attribute__system` (system),
  UNIQUE KEY `unique__index_attribute__collection_id__attribute_id__type__name` (collection_id, attribute_id, type, name),
  PRIMARY KEY (index_attribute_id)
);