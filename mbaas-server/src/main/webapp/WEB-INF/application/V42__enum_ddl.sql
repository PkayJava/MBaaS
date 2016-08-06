CREATE TABLE enum (

  enum_id     VARCHAR(100) NOT NULL,
  name        VARCHAR(100),
  type        VARCHAR(20)  NOT NULL,
  format      VARCHAR(255),
  description VARCHAR(100),

  INDEX (type),
  INDEX (format),
  INDEX (description),
  UNIQUE KEY (name),
  PRIMARY KEY (enum_id)
);