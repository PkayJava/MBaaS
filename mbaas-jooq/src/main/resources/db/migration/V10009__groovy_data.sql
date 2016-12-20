# groovy table

INSERT INTO groovy (groovy_id, java_class, script_crc32, script, system)
VALUES
  ('com.angkorteam.mbaas.server.page.DashboardPage',
   'com.angkorteam.mbaas.server.page.DashboardPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.menu.MenuBrowsePage',
   'com.angkorteam.mbaas.server.page.menu.MenuBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.menu.MenuCreatePage',
   'com.angkorteam.mbaas.server.page.menu.MenuCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.menu.MenuModifyPage',
   'com.angkorteam.mbaas.server.page.menu.MenuModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.menuitem.MenuItemBrowsePage',
   'com.angkorteam.mbaas.server.page.menuitem.MenuItemBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.menuitem.MenuItemCreatePage',
   'com.angkorteam.mbaas.server.page.menuitem.MenuItemCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.menuitem.MenuItemModifyPage',
   'com.angkorteam.mbaas.server.page.menuitem.MenuItemModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.section.SectionBrowsePage',
   'com.angkorteam.mbaas.server.page.section.SectionBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.section.SectionCreatePage',
   'com.angkorteam.mbaas.server.page.section.SectionCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.section.SectionModifyPage',
   'com.angkorteam.mbaas.server.page.section.SectionModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.layout.LayoutBrowsePage',
   'com.angkorteam.mbaas.server.page.layout.LayoutBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.layout.LayoutCreatePage',
   'com.angkorteam.mbaas.server.page.layout.LayoutCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.layout.LayoutModifyPage',
   'com.angkorteam.mbaas.server.page.layout.LayoutModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.page.PageModifyPage',
   'com.angkorteam.mbaas.server.page.page.PageModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.page.PageCreatePage',
   'com.angkorteam.mbaas.server.page.page.PageCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.page.PageBrowsePage',
   'com.angkorteam.mbaas.server.page.page.PageBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.page.RoleModifyPage',
   'com.angkorteam.mbaas.server.page.page.RoleModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.role.RoleCreatePage',
   'com.angkorteam.mbaas.server.page.role.RoleCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.role.RoleBrowsePage',
   'com.angkorteam.mbaas.server.page.role.RoleBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.collection.CollectionBrowsePage',
   'com.angkorteam.mbaas.server.page.collection.CollectionBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.collection.CollectionCreatePage',
   'com.angkorteam.mbaas.server.page.collection.CollectionCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.attribute.AttributeCreatePage',
   'com.angkorteam.mbaas.server.page.attribute.AttributeCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.attribute.AttributeBrowsePage',
   'com.angkorteam.mbaas.server.page.attribute.AttributeBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.document.DocumentBrowsePage',
   'com.angkorteam.mbaas.server.page.document.DocumentBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.document.DocumentCreatePage',
   'com.angkorteam.mbaas.server.page.document.DocumentCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.document.DocumentModifyPage',
   'com.angkorteam.mbaas.server.page.document.DocumentModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.file.FileModifyPage',
   'com.angkorteam.mbaas.server.page.file.FileModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.file.FileCreatePage',
   'com.angkorteam.mbaas.server.page.file.FileCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.file.FileBrowsePage',
   'com.angkorteam.mbaas.server.page.file.FileBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.groovy.UserBrowsePage',
   'com.angkorteam.mbaas.server.groovy.UserBrowsePage', 1, 'package com.angkorteam.mbaas.server.groovy

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.*
import com.angkorteam.mbaas.server.Spring
import com.angkorteam.mbaas.server.page.CmsPage
import com.angkorteam.mbaas.server.provider.JdbcProvider
import com.google.common.collect.Maps
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm
import org.apache.wicket.lambda.WicketBiFunction
import org.apache.wicket.markup.html.border.Border
import org.apache.wicket.markup.html.link.BookmarkablePageLink
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate

class UserBrowsePage extends CmsPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBrowsePage.class)

    DataTable<Map<String, Object>, String> dataTable

    @Override
    protected void doInitialize(Border layout) {
        add(layout)

        JdbcProvider provider = new JdbcProvider("user LEFT JOIN role ON user.role_id = role.role_id")
        provider.boardField("user.user_id", "userId", String.class)
        provider.boardField("user.full_name", "fullName", String.class)
        provider.boardField("user.login", "login", String.class)
        provider.boardField("role.name", "roleName", String.class)
        provider.boardField("user.status", "status", Boolean.class)
        provider.boardField("user.system", "system", Boolean.class)

        provider.selectField("userId", String.class)

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider)
        layout.add(filterForm)

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>()
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("fullName"), "fullName", modelValue))
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("login"), "login", modelValue))
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("roleName"), "roleName", modelValue))
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("status"), "status", modelValue))
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("system"), "system", modelValue))
        columns.add(new ActionFilterColumn.Builder(Model.of("action"), actions,
                itemClick)
                .withClickable(clickable)
                .withItemCss(itemCss).build())

        dataTable = new DefaultDataTable<>("table", columns, provider, 20)
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm))
        filterForm.add(dataTable)

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", UserBrowsePage.class)
        layout.add(refreshLink)

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", UserCreatePage.class)
        layout.add(createLink)
    }

    def modelValue = { String name, Map<String, Object> stringObjectMap ->
        stringObjectMap.get(name)
    } as WicketBiFunction<String, Map<String, Object>, ?>

    def actions = {
        Map<String, IModel<String>> actions = Maps.newHashMap()
        actions.put("Reset PWD", Model.of("Reset PWD"))
        actions.put("Edit", Model.of("Edit"))
        actions.put("Delete", Model.of("Delete"))
        return actions
    }

    def itemClick = { String link, Map<String, Object> object, AjaxRequestTarget target ->
        String userId = (String) object.get("userId")
        if ("Edit" == link) {
            PageParameters parameters = new PageParameters()
            parameters.add("userId", userId)
            setResponsePage(UserModifyPage.class, parameters)
        }
        if ("Delete" == link) {
            JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class)
            jdbcTemplate.update("DELETE FROM user WHERE user_id = ?", userId)
            target.add(this.dataTable)
        }
        if ("Reset PWD" == link) {
            PageParameters parameters = new PageParameters()
            parameters.add("userId", userId)
            setResponsePage(UserPasswordPage.class, parameters)
        }
    }

    def clickable = { String link, Map<String, Object> object ->
        Boolean system = (Boolean) object.get("system")
        if ("Edit" == link) {
            return !system
        }
        if ("Delete" == link) {
            return !system
        }
        if ("Reset PWD" == link) {
            return true
        }
        return false
    } as WicketBiFunction<String, Map<String, Object>, Boolean>

    def itemCss = { String link, Map<String, Object> model ->
        if ("Edit" == link || "Reset PWD" == link) {
            return ItemCss.INFO
        } else if ("Delete" == link) {
            return ItemCss.DANGER
        }
        return ItemCss.NONE
    } as WicketBiFunction<String, Map<String, Object>, ItemCss>

    @Override
    final String getPageUUID() {
        // DO NOT MODIFIED
        return "com.angkorteam.mbaas.server.groovy.UserBrowsePage"
    }

}', TRUE),
  ('com.angkorteam.mbaas.server.groovy.UserCreatePage',
   'com.angkorteam.mbaas.server.groovy.UserCreatePage', 1, 'package com.angkorteam.mbaas.server.groovy

import com.angkorteam.framework.extension.wicket.markup.html.form.Button
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel
import com.angkorteam.mbaas.server.Spring
import com.angkorteam.mbaas.server.bean.System
import com.angkorteam.mbaas.server.page.CmsPage
import com.angkorteam.mbaas.server.select2.Item
import com.angkorteam.mbaas.server.select2.JdbcSingleChoiceProvider
import com.angkorteam.mbaas.server.validator.UniqueRecordValidator
import com.google.common.collect.Maps
import org.apache.wicket.lambda.WicketConsumer
import org.apache.wicket.markup.html.border.Border
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.PasswordTextField
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator
import org.apache.wicket.markup.html.link.BookmarkablePageLink
import org.apache.wicket.model.PropertyModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class UserCreatePage extends CmsPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatePage.class)

    String fullName
    TextField<String> fullNameField
    TextFeedbackPanel fullNameFeedback

    String login
    TextField<String> loginField
    TextFeedbackPanel loginFeedback

    String password
    TextField<String> passwordField
    TextFeedbackPanel passwordFeedback

    String retypePassword
    TextField<String> retypePasswordField
    TextFeedbackPanel retypePasswordFeedback

    Item role
    Select2SingleChoice<Item> roleField
    TextFeedbackPanel roleFeedback

    Button saveButton
    Form<Void> form
    BookmarkablePageLink<Void> closeButton

    @Override
    protected void doInitialize(Border layout) {
        add(layout)

        this.form = new Form<>("form")
        layout.add(this.form)

        this.fullNameField = new TextField<>("fullNameField", new PropertyModel<>(this, "fullName"))
        this.fullNameField.setRequired(true)
        this.form.add(fullNameField)
        this.fullNameFeedback = new TextFeedbackPanel("fullNameFeedback", this.fullNameField)
        this.form.add(fullNameFeedback)

        this.loginField = new TextField<>("loginField", new PropertyModel<>(this, "login"))
        this.loginField.add(new UniqueRecordValidator<>("user", "login"))
        this.loginField.setRequired(true)
        this.form.add(loginField)
        this.loginFeedback = new TextFeedbackPanel("loginFeedback", this.loginField)
        this.form.add(loginFeedback)

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"))
        this.form.add(this.passwordField)
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField)
        this.form.add(this.passwordFeedback)

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"))
        this.form.add(retypePasswordField)
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField)
        this.form.add(retypePasswordFeedback)

        this.roleField = new Select2SingleChoice<>("roleField", new PropertyModel<>(this, "role"), new JdbcSingleChoiceProvider("role", "role_id", "name"))
        this.roleField.setRequired(true)
        this.form.add(this.roleField)
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField)
        this.form.add(this.roleFeedback)

        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField))

        this.saveButton = new Button("saveButton")
        this.saveButton.setOnSubmit(saveButtonOnSubmit)
        this.form.add(this.saveButton)

        this.closeButton = new BookmarkablePageLink<>("closeButton", UserBrowsePage.class)
        this.form.add(this.closeButton)
    }

    def saveButtonOnSubmit = { Button button ->
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class)
        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate)
        System system = Spring.getBean(System.class)
        Map<String, Object> params = Maps.newHashMap()
        params.put("user_id", system.randomUUID())
        params.put("account_non_expired", true)
        params.put("system", false)
        params.put("account_non_locked", true)
        params.put("credentials_non_expired", true)
        params.put("status", "ACTIVE")
        params.put("login", this.login)
        params.put("password", this.password)
        params.put("full_name", this.fullName)
        params.put("role_id", this.role.getId())
        named.update("INSERT INTO user(user_id, account_non_expired, system, account_non_locked, credentials_non_expired, status, login, password, full_name, role_id) VALUES(:user_id, :account_non_expired, :system, :account_non_locked, :credentials_non_expired, :status, :login, MD5(:password), :full_name, :role_id)", params)
        setResponsePage(UserBrowsePage.class)
    } as WicketConsumer<Button>

    @Override
    final String getPageUUID() {
        // DO NOT MODIFIED
        return "com.angkorteam.mbaas.server.groovy.UserCreatePage"
    }

}', TRUE),
  ('com.angkorteam.mbaas.server.groovy.UserModifyPage',
   'com.angkorteam.mbaas.server.groovy.UserModifyPage', 1, 'package com.angkorteam.mbaas.server.groovy

import com.angkorteam.framework.extension.wicket.markup.html.form.Button
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel
import com.angkorteam.mbaas.server.Spring
import com.angkorteam.mbaas.server.page.CmsPage
import com.angkorteam.mbaas.server.select2.Item
import com.angkorteam.mbaas.server.select2.JdbcSingleChoiceProvider
import com.google.common.collect.Maps
import org.apache.wicket.lambda.WicketConsumer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.border.Border
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.link.BookmarkablePageLink
import org.apache.wicket.model.PropertyModel
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class UserModifyPage extends CmsPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserModifyPage.class)

    String userId

    String fullName
    TextField<String> fullNameField
    TextFeedbackPanel fullNameFeedback

    String login
    Label loginLabel

    Item role
    Select2SingleChoice<Item> roleField
    TextFeedbackPanel roleFeedback

    Button saveButton
    Form<Void> form
    BookmarkablePageLink<Void> closeButton

    @Override
    protected void doInitialize(Border layout) {
        add(layout)

        BeanPropertyRowMapper<Item> mapper = new BeanPropertyRowMapper<>(Item.class)

        PageParameters parameters = getPageParameters()
        this.userId = parameters.get("userId").toString("")

        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class)

        Map<String, Object> userRecord = jdbcTemplate.queryForMap("SELECT * FROM user WHERE user_id = ?", this.userId)

        this.form = new Form<>("form")
        layout.add(this.form)

        this.fullName = userRecord.get("full_name")
        this.fullNameField = new TextField<>("fullNameField", new PropertyModel<>(this, "fullName"))
        this.fullNameField.setRequired(true)
        this.form.add(fullNameField)
        this.fullNameFeedback = new TextFeedbackPanel("fullNameFeedback", this.fullNameField)
        this.form.add(fullNameFeedback)

        this.login = userRecord.get("login")
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"))
        this.form.add(loginLabel)

        try {
            this.role = jdbcTemplate.queryForObject("SELECT role_id id, name value FROM role WHERE role_id = ?", mapper, userRecord.get("role_id") == null ? "" : userRecord.get("role_id"))
        } catch (EmptyResultDataAccessException e) {
        }
        this.roleField = new Select2SingleChoice<>("roleField", new PropertyModel<>(this, "role"), new JdbcSingleChoiceProvider("role", "role_id", "name"))
        this.roleField.setRequired(true)
        this.form.add(this.roleField)
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField)
        this.form.add(this.roleFeedback)

        this.saveButton = new Button("saveButton")
        this.saveButton.setOnSubmit(saveButtonOnSubmit)
        this.form.add(this.saveButton)

        this.closeButton = new BookmarkablePageLink<>("closeButton", UserBrowsePage.class)
        this.form.add(this.closeButton)
    }

    def saveButtonOnSubmit = { Button button ->
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class)
        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate)
        Map<String, Object> params = Maps.newHashMap()
        params.put("user_id", this.userId)
        params.put("full_name", this.fullName)
        params.put("role_id", this.role.getId())
        named.update("UPDATE user SET full_name = :full_name, role_id = :role_id WHERE user_id = :user_id", params)
        setResponsePage(UserBrowsePage.class)
    } as WicketConsumer<Button>

    @Override
    final String getPageUUID() {
        // DO NOT MODIFIED
        return "com.angkorteam.mbaas.server.groovy.UserModifyPage"
    }

}', TRUE),
  ('com.angkorteam.mbaas.server.groovy.UserPasswordPage',
   'com.angkorteam.mbaas.server.groovy.UserPasswordPage', 1, 'package com.angkorteam.mbaas.server.groovy

import com.angkorteam.framework.extension.wicket.markup.html.form.Button
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel
import com.angkorteam.mbaas.server.Spring
import com.angkorteam.mbaas.server.page.CmsPage
import com.google.common.collect.Maps
import org.apache.wicket.lambda.WicketConsumer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.border.Border
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.PasswordTextField
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator
import org.apache.wicket.markup.html.link.BookmarkablePageLink
import org.apache.wicket.model.PropertyModel
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class UserPasswordPage extends CmsPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPasswordPage.class)

    String userId

    String fullName
    Label fullNameLabel

    String login
    Label loginLabel

    String password
    TextField<String> passwordField
    TextFeedbackPanel passwordFeedback

    String retypePassword
    TextField<String> retypePasswordField
    TextFeedbackPanel retypePasswordFeedback

    Button saveButton
    Form<Void> form
    BookmarkablePageLink<Void> closeButton

    @Override
    protected void doInitialize(Border layout) {
        add(layout)

        this.form = new Form<>("form")
        layout.add(this.form)

        PageParameters parameters = getPageParameters()
        this.userId = parameters.get("userId").toString("")

        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class)

        Map<String, Object> userRecord = jdbcTemplate.queryForMap("SELECT * FROM user WHERE user_id = ?", this.userId)

        this.fullName = userRecord.get("full_name")
        this.fullNameLabel = new Label("fullNameLabel", new PropertyModel<>(this, "fullName"))
        this.form.add(fullNameLabel)

        this.login = userRecord.get("login")
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"))
        this.form.add(loginLabel)

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"))
        this.form.add(this.passwordField)
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField)
        this.form.add(this.passwordFeedback)

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"))
        this.form.add(retypePasswordField)
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField)
        this.form.add(retypePasswordFeedback)

        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField))

        this.saveButton = new Button("saveButton")
        this.saveButton.setOnSubmit(saveButtonOnSubmit)
        this.form.add(this.saveButton)

        this.closeButton = new BookmarkablePageLink<>("closeButton", UserBrowsePage.class)
        this.form.add(this.closeButton)
    }

    def saveButtonOnSubmit = { Button button ->
        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class)
        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate)
        Map<String, Object> params = Maps.newHashMap()
        params.put("user_id", this.userId)
        params.put("password", this.password)
        named.update("UPDATE user SET password = MD5(:password) WHERE user_id = :user_id", params)
        setResponsePage(UserBrowsePage.class)
    } as WicketConsumer<Button>

    @Override
    final String getPageUUID() {
        // DO NOT MODIFIED
        return "com.angkorteam.mbaas.server.groovy.UserPasswordPage"
    }

}', TRUE),
  ('com.angkorteam.mbaas.server.page.LoginPage',
   'com.angkorteam.mbaas.server.page.LoginPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.LogoutPage',
   'com.angkorteam.mbaas.server.page.LogoutPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.rest.RestBrowsePage',
   'com.angkorteam.mbaas.server.page.rest.RestBrowsePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.rest.RestCreatePage',
   'com.angkorteam.mbaas.server.page.rest.RestCreatePage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.page.rest.RestModifyPage',
   'com.angkorteam.mbaas.server.page.rest.RestModifyPage', NULL, '', TRUE),
  ('com.angkorteam.mbaas.server.groovy.EmptyLayout', 'com.angkorteam.mbaas.server.groovy.EmptyLayout', 1, 'package com.angkorteam.mbaas.server.groovy
import com.angkorteam.mbaas.server.page.CmsLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EmptyLayout extends CmsLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmptyLayout.class)

    EmptyLayout(String id) {
        super(id)
    }

    @Override
    protected void doInitialize() {
        // place your initialization logic here
    }

    @Override
    final String getLayoutUUID () {
        // DO NOT MODIFIED
        return "com.angkorteam.mbaas.server.groovy.EmptyLayout"
    }

}',
   TRUE),
  ('com.angkorteam.mbaas.server.groovy.SettingPage', 'com.angkorteam.mbaas.server.groovy.SettingPage', 1, 'package com.angkorteam.mbaas.server.groovy

import com.angkorteam.framework.extension.wicket.ajax.markup.html.form.AjaxButton
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel
import com.angkorteam.mbaas.server.Spring
import com.angkorteam.mbaas.server.bean.System
import com.angkorteam.mbaas.server.page.CmsPage
import com.angkorteam.mbaas.server.select2.Item
import com.angkorteam.mbaas.server.select2.JdbcSingleChoiceProvider
import com.google.common.collect.Maps
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.lambda.WicketBiConsumer
import org.apache.wicket.markup.html.border.Border
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.model.PropertyModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.sql2o.Connection
import org.sql2o.Query
import org.sql2o.Sql2o

class SettingPage extends CmsPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingPage.class)

    Item homePage
    Select2SingleChoice<Item> homePageField
    TextFeedbackPanel homePageFeedback

    AjaxButton saveButton
    Form<Void> form

    @Override
    protected void doInitialize(Border layout) {
        add(layout)
        // place your initialization logic here

        this.form = new Form<>("form")
        layout.add(this.form)

        this.homePageField = new Select2SingleChoice<>("homePageField", new PropertyModel<>(this, "homePage"), new JdbcSingleChoiceProvider("page", "page_id", "title"))
        this.homePageField.setRequired(true)
        this.form.add(this.homePageField)
        this.homePageFeedback = new TextFeedbackPanel("homePageFeedback", this.homePageField)
        this.form.add(this.homePageFeedback)

        this.saveButton = new AjaxButton("saveButton")
        this.saveButton.setOnError(saveButtonError)
        this.saveButton.setOnSubmit(saveButtonSubmit)
        this.form.add(this.saveButton)

        JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class)
        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate)
        System system = Spring.getBean(System.class)

        loadSetting(system, jdbcTemplate, named)
    }

    void loadSetting(System system, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate named) {
        loadHomePage(system, jdbcTemplate, named)
    }

    void loadHomePage(System system, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate named) {
        BeanPropertyRowMapper<Item> itemMapper = new BeanPropertyRowMapper<>(Item.class)
        Map<String, Object> params = Maps.newHashMap()
        params.put("name", "home_page")
        try {
            this.homePage = named.queryForObject("SELECT page.page_id id, page.title value FROM setting INNER JOIN page on setting.value = page.page_id where setting.name = :name", params, itemMapper)
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO setting(setting_id, name, value) VALUES(?,?,?)", system.randomUUID(), "home_page", "")
        }
    }

    def saveButtonSubmit = { AjaxButton button, AjaxRequestTarget target ->
        saveHomePage()
        setResponsePage(SettingPage.class)
    } as WicketBiConsumer<AjaxButton, AjaxRequestTarget>

    void saveHomePage() {
        Sql2o sql2o = Spring.getBean(Sql2o.class)
        Connection connection = sql2o.beginTransaction()
        connection.withCloseable {
            Query query = connection.createQuery("UPDATE setting set value = :value where name = :name")
            query.addParameter("name", "home_page")
            query.addParameter("value", this.homePage.getId())
            query.executeUpdate()
            connection.commit()
        }
    }

    def saveButtonError = { AjaxButton button, AjaxRequestTarget target ->
        target.add(form)
    } as WicketBiConsumer<AjaxButton, AjaxRequestTarget>

    @Override
    final String getPageUUID() {
        // DO NOT MODIFIED
        return "com.angkorteam.mbaas.server.groovy.SettingPage"
    }

}', TRUE),
  ('com.angkorteam.mbaas.server.groovy.IndexPage',
   'com.angkorteam.mbaas.server.groovy.IndexPage', 1, 'package com.angkorteam.mbaas.server.groovy

import com.angkorteam.mbaas.server.page.CmsPage
import org.apache.wicket.markup.html.border.Border
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class IndexPage extends CmsPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexPage.class)

    @Override
    protected void doInitialize(Border layout) {
        add(layout)
        // place your initialization logic here
    }

    @Override
    final String getPageUUID() {
        // DO NOT MODIFIED
        return "com.angkorteam.mbaas.server.groovy.IndexPage"
    }

}', TRUE);