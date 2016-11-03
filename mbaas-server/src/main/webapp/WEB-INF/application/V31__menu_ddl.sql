CREATE TABLE menu (

  menu_id        VARCHAR(100) NOT NULL,
  title          VARCHAR(100) NOT NULL,
  user_id        VARCHAR(100) NOT NULL,
  parent_menu_id VARCHAR(100),
  date_created   DATETIME     NOT NULL,

  INDEX (title),
  INDEX (parent_menu_id),
  INDEX (user_id),
  INDEX (date_created),
  PRIMARY KEY (menu_id)
);