#MUTABLE
CREATE TABLE `instance_attribute` (

  instance_attribute_id VARCHAR(100) NOT NULL,
  collection_id         VARCHAR(100) NOT NULL,
  attribute_id          VARCHAR(100) NOT NULL,
  `order`               INT(11)      NOT NULL DEFAULT 0,
  system                BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__instance_attribute__order` (`order`),
  KEY `index__instance_attribute__system` (system),
  UNIQUE KEY `unique__instance_attribute__attribute_id__collection_id` (attribute_id, collection_id),
  PRIMARY KEY (instance_attribute_id)
);