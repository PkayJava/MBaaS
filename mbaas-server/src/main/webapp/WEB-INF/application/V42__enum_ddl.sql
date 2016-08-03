CREATE TABLE enum (

  enum_id         VARCHAR(100) NOT NULL,
  name            VARCHAR(100),
  -- Boolean, Long, Double, String, Date, Time, DateTime
  type            VARCHAR(100) NOT NULL,
  --
  format_date     VARCHAR(100),
  format_time     VARCHAR(100),
  format_datetime VARCHAR(100),
  format_number   VARCHAR(100),
  format_string   VARCHAR(255),
  
  description     VARCHAR(100),

  PRIMARY KEY (enum_id)
);