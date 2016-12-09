# layout table
INSERT INTO layout (layout_id, title, description, groovy_id, html, html_crc32, modified, date_created, date_modified, cms_layout, system)
VALUES
  ('com.angkorteam.mbaas.server.page.MBaaSLayout', 'MBaaS Layout', 'MBaaS Layout', NULL, '', NULL, FALSE, now(),
                                                   now(), FALSE, TRUE),
  ('com.angkorteam.mbaas.server.groovy.EmptyLayout', 'Empty Layout', 'Empty Layout',
                                                   'com.angkorteam.mbaas.server.groovy.EmptyLayout', '<?xml version="1.0" encoding="UTF-8" ?>
<html xmlns:wicket="http://wicket.apache.org">
<wicket:border>

    <!-- header -->

    <wicket:body/>

    <!-- footer-->

</wicket:border>
</html>', 1597099448, FALSE,
                                                   now(),
                                                   now(), TRUE, FALSE);