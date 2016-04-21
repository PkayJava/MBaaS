CREATE TABLE statistics (

  statistics_id VARCHAR(100),
  cpu           DECIMAL(15, 4),
  memory        DECIMAL(15, 4),
  date_created  DATETIME NOT NULL,

  INDEX (cpu),
  INDEX (memory),
  INDEX (date_created),
  PRIMARY KEY (statistics_id)
);