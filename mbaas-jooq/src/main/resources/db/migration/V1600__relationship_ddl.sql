#MUTABLE
CREATE TABLE `relationship` (

  relationship_id           VARCHAR(100) NOT NULL,
  collection_id             VARCHAR(100) NOT NULL,
  attribute_id              VARCHAR(100) NOT NULL,
  name                      VARCHAR(100) NOT NULL,
  join_type                 VARCHAR(10)  NOT NULL,
  join_collection_id        VARCHAR(100) NOT NULL,
  join_attribute_id         VARCHAR(100) NOT NULL,
  many_collection_id        VARCHAR(100),
  many_join_attribute_id    VARCHAR(100),
  many_reverse_attribute_id VARCHAR(100),
  system                    BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__relationship__system` (system),
  KEY `index__relationship__attribute_id` (attribute_id),
  KEY `index__relationship__join_type` (join_type),
  KEY `index__relationship__join_collection_id` (join_collection_id),
  KEY `index__relationship__join_attribute_id` (join_attribute_id),
  KEY `index__relationship__many_collection_id` (many_collection_id),
  KEY `index__relationship__many_join_attribute_id` (many_join_attribute_id),
  KEY `index__relationship__many_reverse_attribute_id` (many_reverse_attribute_id),
  UNIQUE KEY `unique__relationship__collection_id__name__attribute_id` (collection_id, name, attribute_id),
  PRIMARY KEY (relationship_id)
);