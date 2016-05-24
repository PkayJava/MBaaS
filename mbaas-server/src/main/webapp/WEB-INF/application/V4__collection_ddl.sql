CREATE TABLE `collection` (

  collection_id             VARCHAR(100) NOT NULL,
  name                      VARCHAR(255) NOT NULL,
  application_code          VARCHAR(100) NOT NULL,
  locked                    BIT(1)       NOT NULL DEFAULT 0,
  system                    BIT(1)       NOT NULL DEFAULT 0,
  owner_application_user_id VARCHAR(100),

  UNIQUE KEY (name),
  INDEX (locked),
  INDEX (application_code),
  INDEX (system),
  INDEX (owner_application_user_id),
  PRIMARY KEY (collection_id)
);