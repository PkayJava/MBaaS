package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.HostnameRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasUserRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.angkorteam.mbaas.server.function.ApplicationFunction;
import com.angkorteam.mbaas.server.page.LoginPage;
import com.angkorteam.mbaas.server.validator.ApplicationDomainValidator;
import com.angkorteam.mbaas.server.validator.ApplicationLoginValidator;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.PropertyModel;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 6/19/16.
 */
@Mount("/mbaas/register")
public class MBaaSRegisterPage extends AdminLTEPage {

    private static final String DOMAIN = ".ddns.net";

    private String server;
    private Label serverLabel;

    private String fqdn;
    private TextField<String> fqdnField;
    private TextFeedbackPanel fqdnFeedback;

    private String login;
    private TextField<String> loginField;
    private TextFeedbackPanel loginFeedback;

    private String password;
    private PasswordTextField passwordField;
    private TextFeedbackPanel passwordFeedback;

    private String retypePassword;
    private PasswordTextField retypePasswordField;
    private TextFeedbackPanel retypePasswordFeedback;

    private String fullName;
    private TextField<String> fullNameField;
    private TextFeedbackPanel fullNameFeedback;

    private Form<Void> form;
    private Button registerButton;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.form = new Form<>("form");
        add(this.form);

        try {
            HttpResponse<String> json = Unirest.get("https://api.ipify.org/?format=json").asString();
            Map<String, Object> gson = ApplicationUtils.getApplication().getGson().fromJson(json.getBody(), new TypeToken<Map<String, Object>>() {
            }.getType());
            this.server = (String) gson.get("ip");
        } catch (UnirestException e) {
        }
        this.serverLabel = new Label("serverLabel", new PropertyModel<>(this, "server"));
        this.form.add(serverLabel);

        this.fqdnField = new TextField<>("fqdnField", new PropertyModel<>(this, "fqdn"));
        this.fqdnField.setRequired(true);
        this.fqdnField.add(new ApplicationDomainValidator(DOMAIN));
        this.form.add(this.fqdnField);
        this.fqdnFeedback = new TextFeedbackPanel("fqdnFeedback", this.fqdnField);
        this.form.add(this.fqdnFeedback);

        this.loginField = new TextField<>("loginField", new PropertyModel<>(this, "login"));
        this.loginField.setRequired(true);
        this.loginField.add(new ApplicationLoginValidator());
        this.form.add(this.loginField);
        this.loginFeedback = new TextFeedbackPanel("loginFeedback", this.loginField);
        this.form.add(this.loginFeedback);

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setRequired(true);
        this.form.add(this.passwordField);
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);
        this.form.add(this.passwordFeedback);

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"));
        this.retypePasswordField.setRequired(true);
        this.form.add(this.retypePasswordField);
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField);
        this.form.add(this.retypePasswordFeedback);

        this.fullNameField = new TextField<>("fullNameField", new PropertyModel<>(this, "fullName"));
        this.fullNameField.setRequired(true);
        this.form.add(this.fullNameField);
        this.fullNameFeedback = new TextFeedbackPanel("fullNameFeedback", this.fullNameField);
        this.form.add(this.fullNameFeedback);

        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField));

        this.registerButton = new Button("registerButton");
        this.registerButton.setOnSubmit(this::registerButtonOnSubmit);
        this.form.add(registerButton);
    }

    private void registerButtonOnSubmit(Button button) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        DSLContext context = ApplicationUtils.getApplication().getDSLContext();
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        MbaasUserTable mbaasUserTable = Tables.MBAAS_USER.as("mbaasUserTable");
        MbaasUserRecord mbaasUserRecord = context.select(mbaasUserTable.fields()).from(mbaasUserTable).where(mbaasUserTable.LOGIN.eq(configuration.getString(Constants.USER_ADMIN))).fetchOneInto(mbaasUserTable);

        String code = this.fqdn.substring(0, this.fqdn.length() - DOMAIN.length());

        String mysqlHostname = configuration.getString(Constants.APP_JDBC_HOSTNAME);
        String mysqlPort = configuration.getString(Constants.APP_JDBC_PORT);
        String mysqlExtra = configuration.getString(Constants.APP_JDBC_EXTRA);
        String mysqlDatabase = StringUtils.lowerCase(code);
        String mysqlUsername = StringUtils.lowerCase(code);
        String mysqlPassword = UUID.randomUUID().toString();

        ApplicationRecord applicationRecord = context.newRecord(applicationTable);
        String applicationId = UUID.randomUUID().toString();
        applicationRecord.setApplicationId(applicationId);
        applicationRecord.setName(code);
        applicationRecord.setCode(code);
        applicationRecord.setDescription(code);
        applicationRecord.setDateCreated(new Date());
        applicationRecord.setSecret(UUID.randomUUID().toString());
        applicationRecord.setSecurity(SecurityEnum.Denied.getLiteral());
        applicationRecord.setMbaasUserId(mbaasUserRecord.getMbaasUserId());
        applicationRecord.setMysqlHostname(mysqlHostname);
        applicationRecord.setMysqlPort(mysqlPort);
        applicationRecord.setMysqlDatabase(mysqlDatabase);
        applicationRecord.setMysqlUsername(mysqlUsername);
        applicationRecord.setMysqlPassword(mysqlPassword);
        applicationRecord.setMysqlExtra(mysqlExtra);
        applicationRecord.store();

        HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
        HostnameRecord hostnameRecord = context.newRecord(hostnameTable);
        hostnameRecord.setHostnameId(UUID.randomUUID().toString());
        hostnameRecord.setDateCreated(new Date());
        hostnameRecord.setApplicationId(applicationId);
        hostnameRecord.setFqdn(StringUtils.lowerCase(code) + ".ddns.net");
        hostnameRecord.store();

        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate();
        jdbcTemplate.execute("CREATE USER '" + mysqlUsername + "'@'%' IDENTIFIED BY '" + mysqlPassword + "'");
        jdbcTemplate.execute("GRANT USAGE ON *.* TO '" + mysqlUsername + "'@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0");
        jdbcTemplate.execute("GRANT ALL PRIVILEGES ON `" + mysqlDatabase + "`.* TO '" + mysqlUsername + "'@'%';");

        DbSupport dbSupport = ApplicationUtils.getApplication().getDbSupport();
        ServletContext servletContext = ApplicationUtils.getApplication().getServletContext();
        ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource = ApplicationUtils.getApplication().getApplicationDataSource();

        ApplicationFunction.createApplication(code, mysqlHostname, mysqlPort, mysqlExtra, mysqlDatabase, mysqlUsername, mysqlPassword, applicationDataSource, dbSupport, servletContext);

        JdbcTemplate applicationJdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(code);
        String roleId = applicationJdbcTemplate.queryForObject("SELECT " + Jdbc.Role.ROLE_ID + " FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.NAME + " = ?", String.class, configuration.getString(Constants.ROLE_ADMINISTRATOR));

        String userId = UUID.randomUUID().toString();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(applicationJdbcTemplate);
        jdbcInsert.withTableName(Jdbc.USER);
        Map<String, Object> user = new HashMap<>();
        user.put(Jdbc.User.USER_ID, userId);
        user.put(Jdbc.User.ACCOUNT_NON_EXPIRED, true);
        user.put(Jdbc.User.SYSTEM, true);
        user.put(Jdbc.User.ACCOUNT_NON_LOCKED, true);
        user.put(Jdbc.User.CREDENTIALS_NON_EXPIRED, true);
        user.put(Jdbc.User.STATUS, UserStatusEnum.Active.getLiteral());
        user.put(Jdbc.User.LOGIN, this.login);
        user.put(Jdbc.User.FULL_NAME, this.fullName);
        user.put(Jdbc.User.PASSWORD, this.password);
        user.put(Jdbc.User.ROLE_ID, roleId);
        user.put(Jdbc.User.AUTHENTICATION, AuthenticationEnum.None.getLiteral());
        jdbcInsert.execute(user);
        applicationJdbcTemplate.update("UPDATE " + Jdbc.USER + " SET " + Jdbc.User.PASSWORD + " = MD5(?) WHERE " + Jdbc.User.USER_ID + " = ?", this.password, userId);

        setResponsePage(LoginPage.class);
    }
}
