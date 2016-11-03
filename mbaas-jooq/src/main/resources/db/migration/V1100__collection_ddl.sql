#MUTABLE
CREATE TABLE `collection` (

  collection_id VARCHAR(100) NOT NULL,
  name          VARCHAR(255) NOT NULL, #INSTANCE
  locked        BIT(1)       NOT NULL DEFAULT 0,
  system        BIT(1)       NOT NULL DEFAULT 0,
  mutable       BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__collection__name` (name),
  KEY `index__collection__locked` (locked),
  KEY `index__collection__system` (system),
  KEY `index__collection__mutable` (mutable),
  PRIMARY KEY (collection_id)
);