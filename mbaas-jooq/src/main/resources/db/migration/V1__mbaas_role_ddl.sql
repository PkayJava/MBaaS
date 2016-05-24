CREATE TABLE mbaas_role (

  mbaas_role_id VARCHAR(100) NOT NULL,
  system        BIT(1)       NOT NULL DEFAULT 0,
  name          VARCHAR(255) NOT NULL,
  description   VARCHAR(255),

  UNIQUE KEY (name),
  INDEX (system),
  INDEX (description),
  PRIMARY KEY (mbaas_role_id)
);