CREATE TABLE job (

  job_id               VARCHAR(100),
  `name`               VARCHAR(255) NOT NULL,
  `cron`               VARCHAR(255) NOT NULL,
  owner_user_id        VARCHAR(100),
  consume              DECIMAL(15, 4),
  `javascript`         TEXT         NOT NULL,
  `error_message`      VARCHAR(255),
  `error_class`        VARCHAR(255),
  `date_last_executed` DATETIME,
  security             VARCHAR(50)  NOT NULL,
  date_created         DATETIME     NOT NULL,
  application_id       VARCHAR(100) NOT NULL,

  UNIQUE (`name`),
  INDEX (`cron`),
  FULLTEXT (`javascript`),
  INDEX (`owner_user_id`),
  INDEX (`consume`),
  INDEX (`error_message`),
  INDEX (`error_class`),
  INDEX (`date_last_executed`),
  INDEX (`security`),
  INDEX (date_created),
  INDEX (application_id),
  PRIMARY KEY (job_id)
);