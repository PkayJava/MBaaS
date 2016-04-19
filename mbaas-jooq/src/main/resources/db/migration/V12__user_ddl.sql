CREATE TABLE user (

  user_id                           VARCHAR(100) NOT NULL,
  login                             VARCHAR(255) NOT NULL,
  password                          VARCHAR(255) NOT NULL,

  role_id                           VARCHAR(255) NOT NULL,

  system                            BIT(1)       NOT NULL DEFAULT 0,

  -- TOTP, 2Factor SMS, 2Factor EMail, None
  authentication                    VARCHAR(20)  NOT NULL DEFAULT 'NONE',

  -- SMS Tow Factors Authentication
  mobile_number                     VARCHAR(100),

  -- EMail Tow Factors Authentication
  email_address                     VARCHAR(200),

  -- TOTP
  totp_secret                       VARCHAR(100),
  totp_hash                         VARCHAR(100),
  totp_status                       VARCHAR(100),

  account_non_expired               BIT(1)       NOT NULL DEFAULT 1,
  account_non_locked                BIT(1)       NOT NULL DEFAULT 1,
  credentials_non_expired           BIT(1)       NOT NULL DEFAULT 1,
  status                            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',

  password_reset_token              VARCHAR(255),
  password_reset_token_expired_date DATETIME,

  optimistic                        INT(11)               DEFAULT 0,

  UNIQUE KEY (login),
  INDEX (password),
  INDEX (role_id),
  INDEX (system),
  INDEX (authentication),
  INDEX (mobile_number),
  INDEX (email_address),
  INDEX (totp_secret),
  INDEX (totp_hash),
  INDEX (totp_status),
  INDEX (account_non_expired),
  INDEX (account_non_locked),
  INDEX (credentials_non_expired),
  INDEX (status),
  INDEX (password_reset_token),
  INDEX (password_reset_token_expired_date),
  INDEX (optimistic),
  PRIMARY KEY (user_id)
);