CREATE TABLE cpu (

  cpu_id       VARCHAR(100),
  user         DECIMAL(15, 4),
  nice         DECIMAL(15, 4),
  system       DECIMAL(15, 4),
  iowait       DECIMAL(15, 4),
  steal        DECIMAL(15, 4),
  idle         DECIMAL(15, 4),

  date_created DATETIME NOT NULL,

  INDEX (user),
  INDEX (nice),
  INDEX (system),
  INDEX (iowait),
  INDEX (steal),
  INDEX (idle),
  INDEX (date_created),
  PRIMARY KEY (cpu_id)
);