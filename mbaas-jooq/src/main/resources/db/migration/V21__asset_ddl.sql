CREATE TABLE asset (

  asset_id      VARCHAR(100) NOT NULL,

  `name`        VARCHAR(255),
  `label`       VARCHAR(255),
  `path`        VARCHAR(255) NOT NULL,
  `mime`        VARCHAR(30),
  `extension`   VARCHAR(10),
  `length`      INT(11)      NOT NULL,

  extra         BLOB,

  date_created  DATETIME     NOT NULL DEFAULT NOW(),

  owner_user_id VARCHAR(100),
  client_id     VARCHAR(100),

  optimistic    INT(11)      NOT NULL DEFAULT 0,

  INDEX (`date_created`),
  INDEX (`name`),
  INDEX (`label`),
  INDEX (`client_id`),
  INDEX (`owner_user_id`),
  INDEX (`path`),
  INDEX (`mime`),
  INDEX (`extension`),
  INDEX (`length`),
  PRIMARY KEY (asset_id)

);