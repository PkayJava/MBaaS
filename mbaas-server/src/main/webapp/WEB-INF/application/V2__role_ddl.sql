CREATE TABLE role (

  role_id      VARCHAR(100) NOT NULL,
  system       BIT(1)       NOT NULL DEFAULT 0,
  name         VARCHAR(255) NOT NULL,
  description  VARCHAR(255),
  home_page_id VARCHAR(100),

  UNIQUE (name),
  INDEX (system),
  INDEX (home_page_id),
  INDEX (description),
  PRIMARY KEY (role_id)
);