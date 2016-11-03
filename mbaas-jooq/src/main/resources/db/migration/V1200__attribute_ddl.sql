#MUTABLE
CREATE TABLE `attribute` (

  attribute_id  VARCHAR(100) NOT NULL,
  collection_id VARCHAR(100) NOT NULL,
  name          VARCHAR(255) NOT NULL, #INSTANCE
  type          VARCHAR(10)  NOT NULL,
  `length`      INT(11),
  `precision`   INT(11),
  `order`       INT(11)      NOT NULL         DEFAULT 0,
  allow_null    BIT(1)       NOT NULL         DEFAULT 1,
  eav           BIT(1)       NOT NULL         DEFAULT 0,
  system        BIT(1)       NOT NULL         DEFAULT 0,

  KEY `index__attribute__order` (`order`),
  KEY `index__attribute__type` (type),
  KEY `index__attribute__eav` (eav),
  KEY `index__attribute__system` (system),
  UNIQUE KEY `unique__attribute__name__collection_id` (name, collection_id),
  PRIMARY KEY (attribute_id)
);