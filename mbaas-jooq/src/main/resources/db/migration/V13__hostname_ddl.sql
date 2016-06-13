CREATE TABLE hostname (

  hostname_id    VARCHAR(100) NOT NULL,
  -- fully qualified domain name
  fqdn           VARCHAR(200) NOT NULL,
  application_id VARCHAR(100) NOT NULL,
  date_created   DATETIME     NOT NULL,

  UNIQUE (fqdn),
  INDEX (application_id),
  INDEX (date_created),
  PRIMARY KEY (hostname_id)
);