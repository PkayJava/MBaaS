#MUTABLE
CREATE TABLE `enum` (

  enum_id     VARCHAR(100) NOT NULL,
  name        VARCHAR(100), #INSTANCE
  type        VARCHAR(20)  NOT NULL,
  format      VARCHAR(255),
  description VARCHAR(100),
  system      BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__enum__system` (system),
  KEY `index__enum__type` (type),
  KEY `index__enum__format` (format),
  KEY `index__enum__description` (description),
  UNIQUE KEY `unique__enum__name` (name),
  PRIMARY KEY (enum_id)
);