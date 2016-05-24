CREATE TABLE disk (

  disk_id      VARCHAR(100),
  `read`       DECIMAL(15, 4),
  `write`      DECIMAL(15, 4),
  device       VARCHAR(100),
  date_created DATETIME NOT NULL,

  INDEX (`read`),
  INDEX (`write`),
  INDEX (device),
  INDEX (date_created),
  PRIMARY KEY (disk_id)
);