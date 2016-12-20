# page table
INSERT INTO page (path, page_id, groovy_id, title, description, layout_id, system, modified, date_created, date_modified, cms_page, html_crc32, html)
VALUES
  ('/dashboard', 'com.angkorteam.mbaas.server.page.DashboardPage',
                 'com.angkorteam.mbaas.server.page.DashboardPage',
                 'Dashboard', 'Dashboard',
                 'com.angkorteam.mbaas.server.page.MBaaSLayout',
                 TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/menu/browse', 'com.angkorteam.mbaas.server.page.menu.MenuBrowsePage',
                   'com.angkorteam.mbaas.server.page.menu.MenuBrowsePage',
                   'Menu Browse', 'Menu Browse',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/menu/create', 'com.angkorteam.mbaas.server.page.menu.MenuCreatePage',
                   'com.angkorteam.mbaas.server.page.menu.MenuCreatePage',
                   'Menu Create', 'Menu Create',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/menu/modify', 'com.angkorteam.mbaas.server.page.menu.MenuModifyPage',
                   'com.angkorteam.mbaas.server.page.menu.MenuModifyPage',
                   'Menu Modify', 'Menu Modify',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/menu/item/browse', 'com.angkorteam.mbaas.server.page.menuitem.MenuItemBrowsePage',
                        'com.angkorteam.mbaas.server.page.menuitem.MenuItemBrowsePage',
                        'Menu Item Browse', 'Menu Item Browse',
                        'com.angkorteam.mbaas.server.page.MBaaSLayout',
                        TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/menu/item/create', 'com.angkorteam.mbaas.server.page.menuitem.MenuItemCreatePage',
                        'com.angkorteam.mbaas.server.page.menuitem.MenuItemCreatePage',
                        'Menu Item Create', 'Menu Item Create',
                        'com.angkorteam.mbaas.server.page.MBaaSLayout',
                        TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/menu/item/modify', 'com.angkorteam.mbaas.server.page.menuitem.MenuItemModifyPage',
                        'com.angkorteam.mbaas.server.page.menuitem.MenuItemModifyPage',
                        'Menu Item Modify', 'Menu Item Modify',
                        'com.angkorteam.mbaas.server.page.MBaaSLayout',
                        TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/section/browse', 'com.angkorteam.mbaas.server.page.section.SectionBrowsePage',
                      'com.angkorteam.mbaas.server.page.section.SectionBrowsePage',
                      'Section Browse', 'Section Browse',
                      'com.angkorteam.mbaas.server.page.MBaaSLayout',
                      TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/section/create', 'com.angkorteam.mbaas.server.page.section.SectionCreatePage',
                      'com.angkorteam.mbaas.server.page.section.SectionCreatePage',
                      'Section Create', 'Section Create',
                      'com.angkorteam.mbaas.server.page.MBaaSLayout',
                      TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/section/modify', 'com.angkorteam.mbaas.server.page.section.SectionModifyPage',
                      'com.angkorteam.mbaas.server.page.section.SectionModifyPage',
                      'Section Modify', 'Section Modify',
                      'com.angkorteam.mbaas.server.page.MBaaSLayout',
                      TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/layout/browse', 'com.angkorteam.mbaas.server.page.layout.LayoutBrowsePage',
                     'com.angkorteam.mbaas.server.page.layout.LayoutBrowsePage',
                     'Layout Browse', 'Layout Browse',
                     'com.angkorteam.mbaas.server.page.MBaaSLayout',
                     TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/layout/create', 'com.angkorteam.mbaas.server.page.layout.LayoutCreatePage',
                     'com.angkorteam.mbaas.server.page.layout.LayoutCreatePage',
                     'Layout Create', 'Layout Create',
                     'com.angkorteam.mbaas.server.page.MBaaSLayout',
                     TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/layout/modify', 'com.angkorteam.mbaas.server.page.layout.LayoutModifyPage',
                     'com.angkorteam.mbaas.server.page.layout.LayoutModifyPage',
                     'Layout Modify', 'Layout Modify',
                     'com.angkorteam.mbaas.server.page.MBaaSLayout',
                     TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/page/modify', 'com.angkorteam.mbaas.server.page.page.PageModifyPage',
                   'com.angkorteam.mbaas.server.page.page.PageModifyPage',
                   'Page Modify', 'Page Modify',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/page/create', 'com.angkorteam.mbaas.server.page.page.PageCreatePage',
                   'com.angkorteam.mbaas.server.page.page.PageCreatePage',
                   'Page Create', 'Page Create',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/page/browse', 'com.angkorteam.mbaas.server.page.page.PageBrowsePage',
                   'com.angkorteam.mbaas.server.page.page.PageBrowsePage',
                   'Page Browse', 'Page Browse',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/role/browse', 'com.angkorteam.mbaas.server.page.role.RoleModifyPage',
                   'com.angkorteam.mbaas.server.page.role.RoleModifyPage',
                   'Role Modify', 'Role Modify',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/role/create', 'com.angkorteam.mbaas.server.page.role.RoleCreatePage',
                   'com.angkorteam.mbaas.server.page.role.RoleCreatePage',
                   'Role Create', 'Role Create',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/role/modify', 'com.angkorteam.mbaas.server.page.role.RoleBrowsePage',
                   'com.angkorteam.mbaas.server.page.role.RoleBrowsePage',
                   'Role Browse', 'Role Browse',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/collection/browse', 'com.angkorteam.mbaas.server.page.collection.CollectionBrowsePage',
                         'com.angkorteam.mbaas.server.page.collection.CollectionBrowsePage',
                         'Collection Browse', 'Collection Browse',
                         'com.angkorteam.mbaas.server.page.MBaaSLayout',
                         TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/collection/create', 'com.angkorteam.mbaas.server.page.collection.CollectionCreatePage',
                         'com.angkorteam.mbaas.server.page.collection.CollectionCreatePage',
                         'Collection Create', 'Collection Create',
                         'com.angkorteam.mbaas.server.page.MBaaSLayout',
                         TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/attribute/create', 'com.angkorteam.mbaas.server.page.attribute.AttributeCreatePage',
                        'com.angkorteam.mbaas.server.page.attribute.AttributeCreatePage',
                        'Attribute Create', 'Attribute Create',
                        'com.angkorteam.mbaas.server.page.MBaaSLayout',
                        TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/attribute/browse', 'com.angkorteam.mbaas.server.page.attribute.AttributeBrowsePage',
                        'com.angkorteam.mbaas.server.page.attribute.AttributeBrowsePage',
                        'Attribute Browse', 'Attribute Browse',
                        'com.angkorteam.mbaas.server.page.MBaaSLayout',
                        TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/document/browse', 'com.angkorteam.mbaas.server.page.document.DocumentBrowsePage',
                       'com.angkorteam.mbaas.server.page.document.DocumentBrowsePage',
                       'Document Browse', 'Document Browse',
                       'com.angkorteam.mbaas.server.page.MBaaSLayout',
                       TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/document/create', 'com.angkorteam.mbaas.server.page.document.DocumentCreatePage',
                       'com.angkorteam.mbaas.server.page.document.DocumentCreatePage',
                       'Document Create', 'Document Create',
                       'com.angkorteam.mbaas.server.page.MBaaSLayout',
                       TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/document/modify', 'com.angkorteam.mbaas.server.page.document.DocumentModifyPage',
                       'com.angkorteam.mbaas.server.page.document.DocumentModifyPage',
                       'Document Modify', 'Document Modify',
                       'com.angkorteam.mbaas.server.page.MBaaSLayout',
                       TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/file/modify', 'com.angkorteam.mbaas.server.page.file.FileModifyPage',
                   'com.angkorteam.mbaas.server.page.file.FileModifyPage',
                   'File Modify', 'File Modify',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/file/create', 'com.angkorteam.mbaas.server.page.file.FileCreatePage',
                   'com.angkorteam.mbaas.server.page.file.FileCreatePage',
                   'File Create', 'File Create',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/file/browse', 'com.angkorteam.mbaas.server.page.file.FileBrowsePage',
                   'com.angkorteam.mbaas.server.page.file.FileBrowsePage',
                   'File Browse', 'File Browse',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/user/browse', 'com.angkorteam.mbaas.server.groovy.UserBrowsePage',
                   'com.angkorteam.mbaas.server.groovy.UserBrowsePage',
                   'User Browse', 'User Browse',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), TRUE, 1, '<!DOCTYPE html>
<html xmlns:wicket="http://wicket.apache.org">
<head>
    <meta charset="utf-8"/>
</head>
<body>
<wicket:extend>
    <body class="hold-transition skin-blue sidebar-mini">
    <div wicket:id="layout">
        <div class="box box-primary">
            <div class="box-header with-border">
                <a wicket:id="refreshLink">Refresh</a> |
                <a wicket:id="createLink">Create User</a>
            </div>
            <div class="box-body">
                <form wicket:id="filter-form">
                    <table wicket:id="table"/>
                </form>
            </div>
        </div>
    </div>
    </body>
</wicket:extend>
</body>
</html>'),
  ('/user/create', 'com.angkorteam.mbaas.server.groovy.UserCreatePage',
                   'com.angkorteam.mbaas.server.groovy.UserCreatePage',
                   'User Create', 'User Create',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), TRUE, 1, '<!DOCTYPE html>
<html xmlns:wicket="http://wicket.apache.org">
<head>
    <meta charset="utf-8"/>
</head>
<body>
<wicket:extend>
    <body class="hold-transition skin-blue sidebar-mini">
    <div wicket:id="layout">
        <div class="box box-primary">
            <form role="form" wicket:id="form">
                <div class="box-body">
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="fullNameField">Full Name</label>
                        <input wicket:id="fullNameField" type="text" class="form-control"/>
                        <span wicket:id="fullNameFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="loginField">Login</label>
                        <input wicket:id="loginField" type="text" class="form-control"/>
                        <span wicket:id="loginFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="roleField">Role</label>
                        <select wicket:id="roleField" class="form-control"/>
                        <span wicket:id="roleFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="passwordField">Password</label>
                        <input wicket:id="passwordField" type="password" class="form-control"/>
                        <span wicket:id="passwordFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="retypePasswordField">Retype Password</label>
                        <input wicket:id="retypePasswordField" type="password" class="form-control"/>
                        <span wicket:id="retypePasswordFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                </div>
                <div class="box-footer">
                    <button wicket:id="saveButton" type="submit" class="btn btn-info">Save</button>
                    <a class="btn btn-default pull-right" wicket:id="closeButton">Close</a>
                </div>
            </form>
        </div>
    </div>
    </body>
</wicket:extend>
</body>
</html>'),
  ('/user/modify', 'com.angkorteam.mbaas.server.groovy.UserModifyPage',
                   'com.angkorteam.mbaas.server.groovy.UserModifyPage',
                   'User Modify', 'User Modify',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), TRUE, 1, '<!DOCTYPE html>
<html xmlns:wicket="http://wicket.apache.org">
<head>
    <meta charset="utf-8"/>
</head>
<body>
<wicket:extend>
    <body class="hold-transition skin-blue sidebar-mini">
    <div wicket:id="layout">
        <div class="box box-primary">
            <form role="form" wicket:id="form">
                <div class="box-body">
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label>Login</label>
                        <span wicket:id="loginLabel" class="form-control" disabled="true"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="fullNameField">Full Name</label>
                        <input wicket:id="fullNameField" type="text" class="form-control"/>
                        <span wicket:id="fullNameFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="roleField">Role</label>
                        <select wicket:id="roleField" class="form-control"/>
                        <span wicket:id="roleFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                </div>
                <div class="box-footer">
                    <button wicket:id="saveButton" type="submit" class="btn btn-info">Save</button>
                    <a class="btn btn-default pull-right" wicket:id="closeButton">Close</a>
                </div>
            </form>
        </div>
    </div>
    </body>
</wicket:extend>
</body>
</html>'),
  ('/user/password', 'com.angkorteam.mbaas.server.groovy.UserPasswordPage',
                     'com.angkorteam.mbaas.server.groovy.UserPasswordPage',
                     'User Password', 'User Password',
                     'com.angkorteam.mbaas.server.page.MBaaSLayout',
                     TRUE, FALSE, now(), now(), TRUE, 1, '<!DOCTYPE html>
<html xmlns:wicket="http://wicket.apache.org">
<head>
    <meta charset="utf-8"/>
</head>
<body>
<wicket:extend>
    <body class="hold-transition skin-blue sidebar-mini">
    <div wicket:id="layout">
        <div class="box box-primary">
            <form role="form" wicket:id="form">
                <div class="box-body">
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label>Login</label>
                        <span wicket:id="loginLabel" class="form-control" disabled="true"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label>Full Name</label>
                        <span wicket:id="fullNameLabel" class="form-control" disabled="true"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="passwordField">Password</label>
                        <input wicket:id="passwordField" type="password" class="form-control"/>
                        <span wicket:id="passwordFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="retypePasswordField">Retype Password</label>
                        <input wicket:id="retypePasswordField" type="password" class="form-control"/>
                        <span wicket:id="retypePasswordFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                </div>
                <div class="box-footer">
                    <button wicket:id="saveButton" type="submit" class="btn btn-info">Save</button>
                    <a class="btn btn-default pull-right" wicket:id="closeButton">Close</a>
                </div>
            </form>
        </div>
    </div>
    </body>
</wicket:extend>
</body>
</html>'),
  ('/login', 'com.angkorteam.mbaas.server.page.LoginPage',
             'com.angkorteam.mbaas.server.page.LoginPage',
             'Login', 'Login',
             'com.angkorteam.mbaas.server.page.MBaaSLayout',
             TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/logout', 'com.angkorteam.mbaas.server.page.LogoutPage',
              'com.angkorteam.mbaas.server.page.LogoutPage',
              'Logout', 'Logout',
              'com.angkorteam.mbaas.server.page.MBaaSLayout',
              TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/rest/browse', 'com.angkorteam.mbaas.server.page.rest.RestBrowsePage',
                   'com.angkorteam.mbaas.server.page.rest.RestBrowsePage',
                   'Rest Browse', 'Rest Browse',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/rest/create', 'com.angkorteam.mbaas.server.page.rest.RestCreatePage',
                   'com.angkorteam.mbaas.server.page.rest.RestCreatePage',
                   'Rest Create', 'Rest Create',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/rest/modify', 'com.angkorteam.mbaas.server.page.rest.RestModifyPage',
                   'com.angkorteam.mbaas.server.page.rest.RestModifyPage',
                   'Rest Modify', 'Rest Modify',
                   'com.angkorteam.mbaas.server.page.MBaaSLayout',
                   TRUE, FALSE, now(), now(), FALSE, NULL, ''),
  ('/setting', 'com.angkorteam.mbaas.server.groovy.SettingPage',
               'com.angkorteam.mbaas.server.groovy.SettingPage',
               'Setting', 'Setting',
               'com.angkorteam.mbaas.server.page.MBaaSLayout',
               TRUE, FALSE, now(), now(), TRUE, 1, '<!DOCTYPE html>
<html xmlns:wicket="http://wicket.apache.org">
<head>
    <meta charset="utf-8"/>
</head>
<body>
<wicket:extend>
    <body class="hold-transition skin-blue sidebar-mini">
    <div wicket:id="layout">
        <div class="box box-primary">
            <form role="form" wicket:id="form">
                <div class="box-body">
                    <!-- Row -->
                    <div class="form-group col-xl-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <label wicket:for="homePageField">Home Page</label>
                        <select wicket:id="homePageField" class="form-control select2" style="width: 100%;"></select>
                        <span wicket:id="homePageFeedback" class="help-block"/>
                    </div>
                    <div class="clearfix"></div>
                </div>
                <div class="box-footer">
                    <button wicket:id="saveButton" type="submit" class="btn btn-info pull-right">Save</button>
                </div>
            </form>
        </div>
    </div>
    </body>
</wicket:extend>
</body>
</html>'),
  ('/index', 'com.angkorteam.mbaas.server.groovy.IndexPage',
             'com.angkorteam.mbaas.server.groovy.IndexPage',
             'Index', 'Index',
             'com.angkorteam.mbaas.server.groovy.EmptyLayout',
             TRUE, FALSE, now(), now(), TRUE, 1, '<!DOCTYPE html>
<html xmlns:wicket="http://wicket.apache.org">
<head>
    <meta charset="utf-8"/>
</head>
<body>
<wicket:extend>
    <body class="hold-transition skin-blue sidebar-mini">
        <div wicket:id="layout">
            <!-- your page content go here -->
        </div>
    </body>
</wicket:extend>
</body>
</html>');