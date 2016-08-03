CREATE TABLE request_query (

  request_query_id  VARCHAR(100) NOT NULL,
  name              VARCHAR(100),
  type              VARCHAR(100),
  type_enum         VARCHAR(100),
  format            VARCHAR(100),
  maximum           DECIMAL(15, 4),
  exclusive_maximum BIT(1),
  minimum           DECIMAL(15, 4),
  exclusive_minimum BIT(1),
  max_length        INT(11),
  min_length        INT(11),
  pattern           VARCHAR(255),
  max_items         INT(11),
  min_items         INT(11),
  unique_items      BIT(1),
  `enum`            TEXT,

  PRIMARY KEY (request_query_id)
);