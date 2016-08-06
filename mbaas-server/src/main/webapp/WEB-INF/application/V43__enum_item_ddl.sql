CREATE TABLE enum_item (

  enum_item_id VARCHAR(100) NOT NULL,
  enum_id      VARCHAR(100) NOT NULL,
  value        VARCHAR(255) NOT NULL,

  UNIQUE KEY (enum_id, value),
  PRIMARY KEY (enum_item_id)
);