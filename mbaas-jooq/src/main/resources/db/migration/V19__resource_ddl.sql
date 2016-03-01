CREATE TABLE resource (

  resource_id VARCHAR(100) NOT NULL,

  `key`       VARCHAR(100) NOT NULL,
  `page`      VARCHAR(255),
  `language`  VARCHAR(10),
  `label`     VARCHAR(255) NOT NULL,

  UNIQUE KEY (`key`, page, language),
  PRIMARY KEY (resource_id)

);