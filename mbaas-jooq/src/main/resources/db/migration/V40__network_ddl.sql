CREATE TABLE network (

  network_id   VARCHAR(100),
  consume      DECIMAL(15, 4),
  uri          VARCHAR(255),
  user_agent   VARCHAR(255),
  remote_ip    VARCHAR(50),
  date_created DATETIME NOT NULL,

  INDEX (consume),
  INDEX (remote_ip),
  INDEX (uri),
  INDEX (user_agent),
  INDEX (date_created),
  PRIMARY KEY (network_id)
);