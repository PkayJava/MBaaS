CREATE TABLE request_header (

  request_header_id VARCHAR(100) NOT NULL,
  name              VARCHAR(100),
  -- Long, Double, String, Date, Time, DateTime, Boolean, Enum, Array
  type              VARCHAR(100),
  -- Long, Double, String, Date, Time, DateTime, Boolean, Enum
  type_array        VARCHAR(100),
  -- Long, Double, String, Date, Time, DateTime, Boolean
  enum_id           VARCHAR(100),

  max_items         INT(11),
  min_items         INT(11),
  unique_items      BIT(1),

  --
  format_date       VARCHAR(100),
  format_time       VARCHAR(100),
  format_datetime   VARCHAR(100),
  format_number     VARCHAR(100),
  format_string     VARCHAR(255),

  maximum_number    DECIMAL(15, 4),
  minimum_number    DECIMAL(15, 4),
  maximum_date      DECIMAL(15, 4),
  minimum_date      DECIMAL(15, 4),
  maximum_time      DECIMAL(15, 4),
  minimum_time      DECIMAL(15, 4),
  maximum_datetime  DECIMAL(15, 4),
  minimum_datetime  DECIMAL(15, 4),
  exclusive_maximum BIT(1),
  exclusive_minimum BIT(1),

  maximum_length    INT(11),
  minimum_length    INT(11),

  PRIMARY KEY (request_header_id)
);