CREATE TABLE `index` (

  index_id      VARCHAR(100) NOT NULL,

  collection_id VARCHAR(100) NOT NULL,
  name          VARCHAR(255) NOT NULL,
  attribute_id  VARCHAR(100) NOT NULL,

  `unique`      BIT(1)       NOT NULL,
  type          INT(11),

  UNIQUE (collection_id, name, attribute_id),
  PRIMARY KEY (index_id)

);