CREATE TABLE page_role (

  page_role_id VARCHAR(100) NOT NULL,
  page_id      VARCHAR(100) NOT NULL,
  role_id      VARCHAR(100) NOT NULL,

  INDEX (role_id),
  INDEX (page_id),
  UNIQUE (page_id, role_id),
  PRIMARY KEY (page_role_id)
);