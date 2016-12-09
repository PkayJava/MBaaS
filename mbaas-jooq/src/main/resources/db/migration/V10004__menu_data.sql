INSERT INTO menu (menu_id, section_id, parent_menu_id, title, path, system, icon, `order`)
VALUES
  (1, 1, NULL, 'Content', 'Admin Console > Content', TRUE, 'fa-align-justify', 1),
  (2, 1, NULL, 'Security', 'Admin Console > Security', TRUE, 'fa-lock', 2),
  (3, 1, NULL, 'Database', 'Admin Console > Database', TRUE, 'fa-database', 3),
  (4, 1, NULL, 'Service', 'Admin Console > Service', TRUE, 'fa-gg', 4);