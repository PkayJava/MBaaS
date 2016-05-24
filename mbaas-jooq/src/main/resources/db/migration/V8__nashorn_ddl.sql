CREATE TABLE nashorn (

  nashorn_id   VARCHAR(255) NOT NULL,
  security     VARCHAR(15)  NOT NULL,
  date_created DATETIME     NOT NULL DEFAULT NOW(),

  INDEX (security),
  INDEX (date_created),
  PRIMARY KEY (nashorn_id)
);