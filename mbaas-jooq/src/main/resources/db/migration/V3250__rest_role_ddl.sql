#MUTABLE
CREATE TABLE rest_role (

  rest_role_id VARCHAR(100) NOT NULL,
  rest_id      VARCHAR(100) NOT NULL,
  role_id      VARCHAR(100) NOT NULL,
  system       BIT(1)       NOT NULL DEFAULT 0,

  KEY `unique__rest_role__system` (system),
  UNIQUE KEY `unique__rest_role__rest_id__role_id` (rest_id, role_id),
  PRIMARY KEY (rest_role_id)
);