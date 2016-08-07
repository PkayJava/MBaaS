CREATE TABLE json_field (

  json_field_id     VARCHAR(100) NOT NULL,
  json_id           VARCHAR(100) NOT NULL,
  name              VARCHAR(100) NOT NULL,
  description       VARCHAR(255) NOT NULL,
  maximum           INT(11),
  exclusive_maximum BIT(1),
  minimum           INT(11),
  exclusive_minimum BIT(1),
  max_length        INT(11),
  min_length        INT(11),
  pattern           VARCHAR(255),
  max_items         INT(11),
  min_items         INT(11),
  unique_items      BIT(1),
  `enum`            TEXT,
  multiple_of       INT(11),

  UNIQUE KEY (json_id, name),
  PRIMARY KEY (json_field_id)
);