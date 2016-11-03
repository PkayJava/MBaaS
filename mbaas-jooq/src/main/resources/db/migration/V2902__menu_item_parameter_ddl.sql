#MUTABLE
CREATE TABLE menu_item_parameter (

  menu_item_parameter_id VARCHAR(100) NOT NULL,
  menu_item_id           VARCHAR(100) NOT NULL,
  name                   VARCHAR(100) NOT NULL, #INSTANCE
  value                  VARCHAR(255) NOT NULL, #INSTANCE
  system                 BIT(1)       NOT NULL DEFAULT 0,

  KEY `index__menu_item_parameter__system` (system),
  UNIQUE KEY `unique__menu_item_parameter__value__name__menu_item_id` (value, name, menu_item_id),
  PRIMARY KEY (menu_item_parameter_id)
);