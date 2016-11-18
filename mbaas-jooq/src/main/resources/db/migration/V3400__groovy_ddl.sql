#MUTABLE
CREATE TABLE groovy (

  groovy_id    VARCHAR(100) NOT NULL,
  java_class   VARCHAR(255) NOT NULL,
  script       TEXT         NOT NULL,
  script_crc32 VARCHAR(100),
  system       BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__groovy__java_class` (java_class),
  KEY `index__groovy__system` (system),
  KEY `index__groovy__script_crc32` (script_crc32),
  PRIMARY KEY (groovy_id)
);