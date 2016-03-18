CREATE TABLE javascript (

  javascript_id VARCHAR(100) NOT NULL,
  name          VARCHAR(255) NOT NULL,
  path          VARCHAR(50)  NOT NULL,
  description   VARCHAR(255) NOT NULL,

  script        TEXT,
  date_created  DATETIME     NOT NULL DEFAULT NOW(),

  owner_user_id VARCHAR(100) NOT NULL,

  deleted       BIT(1)       NOT NULL DEFAULT 0,
  optimistic    INT(11)      NOT NULL DEFAULT 0,

  UNIQUE KEY (name),
  UNIQUE KEY (path),
  PRIMARY KEY (javascript_id)

);