#MUTABLE
CREATE TABLE menu_item (

  menu_item_id VARCHAR(100) NOT NULL,
  menu_id      VARCHAR(100),
  title        VARCHAR(100) NOT NULL, #INSTANCE
  system       BIT(1)       NOT NULL DEFAULT 0,
  icon         VARCHAR(100),
  `order`      INT(11)      NOT NULL DEFAULT 0,
  page_id      VARCHAR(100) NOT NULL,
  section_id   VARCHAR(100),

  KEY `index__menu_item__title` (title),
  KEY `index__menu_item__order` (`order`),
  KEY `index__menu_item__icon` (icon),
  KEY `index__menu_item__menu_id` (menu_id),
  KEY `index__menu_item__section_id` (section_id),
  KEY `index__menu_item__system` (system),
  UNIQUE KEY `unique__menu_item__page_id` (page_id),
  PRIMARY KEY (menu_item_id)
);