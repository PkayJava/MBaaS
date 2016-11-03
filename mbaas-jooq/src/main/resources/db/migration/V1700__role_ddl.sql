#MUTABLE
CREATE TABLE role (

  role_id     VARCHAR(100) NOT NULL,
  name        VARCHAR(100) NOT NULL, #INSTANCE
  system      BIT(1)       NOT NULL DEFAULT 0,
  description VARCHAR(255),

  KEY `index__role__system` (system),
  KEY `index__role__description` (description),
  UNIQUE KEY `unique__role__name` (name),
  PRIMARY KEY (role_id)
);