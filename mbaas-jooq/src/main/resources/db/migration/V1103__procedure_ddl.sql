#MUTABLE
CREATE TABLE `procedure` (

  procedure_id VARCHAR(100) NOT NULL,
  name         VARCHAR(255) NOT NULL, #INSTANCE
  script       TEXT         NOT NULL,
  system       BIT(1)       NOT NULL DEFAULT 0,

  UNIQUE KEY `unique__procedure__name` (name),
  KEY `index__procedure__system` (system),
  PRIMARY KEY (procedure_id)
);