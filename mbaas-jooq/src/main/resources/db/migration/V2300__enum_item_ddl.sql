#MUTABLE
CREATE TABLE enum_item (

  enum_item_id VARCHAR(100) NOT NULL,
  enum_id      VARCHAR(100) NOT NULL,
  value        VARCHAR(255) NOT NULL,
  system       BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__enum_item__system` (system),
  UNIQUE KEY `unique__enum_item__enum_id__value` (enum_id, value),
  PRIMARY KEY (enum_item_id)
);