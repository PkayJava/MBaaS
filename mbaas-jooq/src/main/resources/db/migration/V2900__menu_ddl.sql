#MUTABLE
CREATE TABLE menu (

  menu_id        VARCHAR(100) NOT NULL,
  title          VARCHAR(100) NOT NULL, #INSTANCE
  path           VARCHAR(255) NOT NULL,
  system         BIT(1)       NOT NULL DEFAULT 0,
  icon           VARCHAR(100),
  section_id     VARCHAR(100),
  parent_menu_id VARCHAR(100),
  `order`        INT(11)      NOT NULL DEFAULT 0,

  KEY `index__menu__title` (title),
  KEY `index__menu__path` (path),
  KEY `index__menu__icon` (icon),
  KEY `index__menu__order` (`order`),
  KEY `index__menu__section_id` (section_id),
  KEY `index__menu__parent_menu_id` (parent_menu_id),
  KEY `index__menu__system` (system),
  PRIMARY KEY (menu_id)
);