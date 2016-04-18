CREATE TABLE application_role (

  application_role_id VARCHAR(100) NOT NULL,

  application_id      VARCHAR(100) NOT NULL,
  name                VARCHAR(100) NOT NULL,

  UNIQUE (name, application_id),
  PRIMARY KEY (application_role_id)

);