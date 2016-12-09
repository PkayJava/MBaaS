INSERT INTO menu_item (menu_item_id, section_id, menu_id, title, system, icon, `order`, page_id)
VALUES
  (1, NULL, 1, 'Section', TRUE, 'fa-leaf', 1, 'com.angkorteam.mbaas.server.page.section.SectionBrowsePage'),
  (2, NULL, 1, 'Menu', TRUE, 'fa-list-ul', 2, 'com.angkorteam.mbaas.server.page.menu.MenuBrowsePage'),
  (3, NULL, 1, 'Menu Item', TRUE, 'fa-home', 3, 'com.angkorteam.mbaas.server.page.menuitem.MenuItemBrowsePage'),
  (4, NULL, 1, 'Layout', TRUE, 'fa-columns', 4, 'com.angkorteam.mbaas.server.page.layout.LayoutBrowsePage'),
  (5, NULL, 1, 'Page', TRUE, 'fa-text-height', 5, 'com.angkorteam.mbaas.server.page.page.PageBrowsePage'),
  (6, NULL, 2, 'Role', TRUE, 'fa-key', 1, 'com.angkorteam.mbaas.server.page.role.RoleBrowsePage'),
  (7, NULL, 3, 'Collection', TRUE, 'fa-cubes', 1, 'com.angkorteam.mbaas.server.page.collection.CollectionBrowsePage'),
  (8, NULL, 1, 'File', TRUE, 'fa-floppy-o', 6, 'com.angkorteam.mbaas.server.page.file.FileBrowsePage'),
  (9, NULL, 2, 'User', TRUE, 'fa-users', 2, 'com.angkorteam.mbaas.server.page.user.UserBrowsePage'),
  (10, NULL, 4, 'Rest', TRUE, 'fa-code', 1, 'com.angkorteam.mbaas.server.page.rest.RestBrowsePage'),
  (11, NULL, 1, 'Setting', TRUE, 'fa-cogs', 7, 'com.angkorteam.mbaas.server.groovy.SettingPage');
