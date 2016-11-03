#MUTABLE
CREATE TABLE page_role (

  page_role_id VARCHAR(100) NOT NULL,
  page_id      VARCHAR(100) NOT NULL,
  role_id      VARCHAR(100) NOT NULL,
  system       BIT(1)       NOT NULL DEFAULT 0,

  KEY `unique__page_role__system` (system),
  UNIQUE KEY `unique__page_role__page_id__role_id` (page_id, role_id),
  PRIMARY KEY (page_role_id)
);