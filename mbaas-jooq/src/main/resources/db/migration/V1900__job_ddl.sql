#MUTABLE
CREATE TABLE job (

  job_id             VARCHAR(100) NOT NULL,
  name               VARCHAR(255) NOT NULL, #INSTANCE
  cron               VARCHAR(255) NOT NULL,
  consume            DECIMAL(15, 4),
  javascript         TEXT         NOT NULL,
  error_message      VARCHAR(255),
  error_class        VARCHAR(255),
  date_last_executed DATETIME,
  security           VARCHAR(50)  NOT NULL,
  date_created       DATETIME     NOT NULL,
  system             BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__job__name` (name),
  KEY `index__job__cron` (cron),
  KEY `index__job__system` (system),
  KEY `index__job__consume` (consume),
  KEY `index__job__error_message` (error_message),
  KEY `index__job__error_class` (error_class),
  KEY `index__job__date_last_executed` (date_last_executed),
  KEY `index__job__security` (security),
  KEY `index__job__date_created` (date_created),
  PRIMARY KEY (job_id)
);