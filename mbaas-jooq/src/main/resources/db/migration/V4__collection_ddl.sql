CREATE TABLE `collection` (

  collection_id VARCHAR(100) NOT NULL,

  name          VARCHAR(255) NOT NULL,

  locked        BIT(1)       NOT NULL DEFAULT 0,
  system        BIT(1)       NOT NULL,
  owner_user_id VARCHAR(100) NOT NULL,

  UNIQUE KEY (name),
  INDEX (owner_user_id),
  PRIMARY KEY (collection_id)

);