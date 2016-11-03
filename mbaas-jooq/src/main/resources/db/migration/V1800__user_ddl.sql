CREATE TABLE user (

  user_id                 VARCHAR(100) NOT NULL,
  login                   VARCHAR(255) NOT NULL, #INSTANCE
  password                VARCHAR(255) NOT NULL,
  full_name               VARCHAR(255) NOT NULL,
  role_id                 VARCHAR(100) NOT NULL,
  system                  BIT(1)       NOT NULL DEFAULT 0,
  account_non_expired     BIT(1)       NOT NULL DEFAULT 1,
  account_non_locked      BIT(1)       NOT NULL DEFAULT 1,
  credentials_non_expired BIT(1)       NOT NULL DEFAULT 1,
  status                  VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',

  UNIQUE KEY `unique__user__login` (login),
  KEY `index__user__full_name` (full_name),
  KEY `index__user__password` (password),
  KEY `index__user__role_id` (role_id),
  KEY `index__user__system` (system),
  KEY `index__user__account_non_expired` (account_non_expired),
  KEY `index__user__account_non_locked` (account_non_locked),
  KEY `index__user__credentials_non_expired` (credentials_non_expired),
  KEY `index__user__status` (status),
  PRIMARY KEY (user_id)
);