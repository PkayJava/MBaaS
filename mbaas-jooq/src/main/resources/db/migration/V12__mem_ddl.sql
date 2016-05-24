CREATE TABLE mem (

  mem_id       VARCHAR(100),
  `device`     VARCHAR(100),
  `total`      DECIMAL(15, 4),
  `used`       DECIMAL(15, 4),
  `free`       DECIMAL(15, 4),
  date_created DATETIME NOT NULL,

  INDEX (`device`),
  INDEX (`total`),
  INDEX (`used`),
  INDEX (`free`),
  INDEX (date_created),
  PRIMARY KEY (mem_id)
);