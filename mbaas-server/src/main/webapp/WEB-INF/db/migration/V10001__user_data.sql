INSERT INTO user (user_id, role_id, login, password, full_name, system, account_non_expired, account_non_locked, credentials_non_expired, status)
VALUES
  (1, 1, 'admin', md5('admin'), 'admin', TRUE, TRUE, TRUE, TRUE, 'ACTIVE'),
  (2, 2, 'system', md5('system'), 'system', TRUE, TRUE, TRUE, TRUE, 'ACTIVE'),
  (3, 3, 'service', md5('service'), 'service', TRUE, TRUE, TRUE, TRUE, 'ACTIVE');