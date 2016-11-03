#MUTABLE
CREATE TABLE security_instantiation (

  security_instantiation_id VARCHAR(100) NOT NULL,
  java_class                VARCHAR(255) NOT NULL, #INSTANCE
  system                    BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__security_instantiation__system` (system),
  UNIQUE KEY `unique__security_instantiation__java_class` (java_class),
  PRIMARY KEY (security_instantiation_id)
);