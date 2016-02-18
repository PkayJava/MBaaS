CREATE TABLE user (

  user_id                           VARCHAR(100) NOT NULL,
  login                             VARCHAR(255) NOT NULL,
  password                          VARCHAR(255) NOT NULL,

  role_id                           VARCHAR(255) NOT NULL,

  account_non_expired               BIT(1)       NOT NULL DEFAULT 1,
  account_non_locked                BIT(1)       NOT NULL DEFAULT 1,
  credentials_non_expired           BIT(1)       NOT NULL DEFAULT 1,
  disabled                          BIT(1)       NOT NULL DEFAULT 1,

  password_reset_token              VARCHAR(255),
  password_reset_token_expired_date DATETIME,

  extra                             BLOB,

  deleted                           BIT(1)       NOT NULL DEFAULT 0,
  optimistic                        INT(11)      NOT NULL DEFAULT 0,

  UNIQUE KEY (login),
  INDEX (password),
  INDEX (deleted),
  INDEX (disabled),
  PRIMARY KEY (user_id)
);