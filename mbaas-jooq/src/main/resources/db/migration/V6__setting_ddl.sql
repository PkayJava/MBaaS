CREATE TABLE setting (

  `setting_id`  VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  `value`       VARCHAR(255) NOT NULL,
  system        BIT(1)       NOT NULL DEFAULT 0,
  optimistic    INT(11)               DEFAULT 0,

  INDEX (description),
  INDEX (value),
  INDEX (system),
  INDEX (optimistic),
  PRIMARY KEY (`setting_id`)
);