CREATE TABLE mbaas_user (

  mbaas_user_id                     VARCHAR(100) NOT NULL,
  login                             VARCHAR(255) NOT NULL,
  password                          VARCHAR(255) NOT NULL,
  full_name                         VARCHAR(255) NOT NULL,
  mbaas_role_id                     VARCHAR(255) NOT NULL,
  system                            BIT(1)       NOT NULL DEFAULT 0,
  authentication                    VARCHAR(20)  NOT NULL DEFAULT 'NONE', -- TOTP, 2Factor SMS, 2Factor EMail, None
  mobile_number                     VARCHAR(100), -- SMS Tow Factors Authentication
  email_address                     VARCHAR(200), -- EMail Tow Factors Authentication
  totp_secret                       VARCHAR(100), -- TOTP
  totp_hash                         VARCHAR(100), -- TOTP
  totp_status                       VARCHAR(100), -- TOTP
  account_non_expired               BIT(1)       NOT NULL DEFAULT 1,
  account_non_locked                BIT(1)       NOT NULL DEFAULT 1,
  credentials_non_expired           BIT(1)       NOT NULL DEFAULT 1,
  status                            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
  password_reset_token              VARCHAR(255),
  password_reset_token_expired_date DATETIME,

  UNIQUE KEY (login),
  INDEX (full_name),
  INDEX (password),
  INDEX (mbaas_role_id),
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
  PRIMARY KEY (mbaas_user_id)
);