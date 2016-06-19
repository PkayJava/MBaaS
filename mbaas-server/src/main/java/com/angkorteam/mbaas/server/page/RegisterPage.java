package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.HostnameRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.validator.HostnameValidator;
import com.angkorteam.mbaas.server.validator.UserLoginFormValidator;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 4/1/16.
 */
@Mount("/register")
public class RegisterPage extends AdminLTEPage {

    private String hostname;
    private TextField<String> hostnameField;
    private TextFeedbackPanel hostnameFeedback;

    private String login;
    private TextField<String> loginField;
    private TextFeedbackPanel loginFeedback;

    private String password;
    private PasswordTextField passwordField;
    private TextFeedbackPanel passwordFeedback;

    private String retypePassword;
    private PasswordTextField retypePasswordField;
    private TextFeedbackPanel retypePasswordFeedback;

    private Button registerButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        DSLContext context = ApplicationUtils.getApplication().getDSLContext();

        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        String hostname = request.getServerName();
        this.hostname = hostname;
        HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
        HostnameRecord hostnameRecord = context.select(hostnameTable.fields()).from(hostnameTable).where(hostnameTable.FQDN.eq(hostname)).fetchOneInto(hostnameTable);

        this.hostnameField = new TextField<>("hostnameField", new PropertyModel<>(this, "hostname"));
        if (hostnameRecord != null) {
            this.hostnameField.setRequired(false);
            this.hostnameField.setVisible(false);
        } else {
            this.hostnameField.setRequired(true);
            this.hostnameField.setVisible(true);
        }
        this.hostnameField.setLabel(JooqUtils.lookup("hostname", this));
        this.hostnameField.add(new HostnameValidator());
        this.form.add(this.hostnameField);
        this.hostnameFeedback = new TextFeedbackPanel("hostnameFeedback", this.hostnameField);
        this.form.add(this.hostnameFeedback);

        this.loginField = new TextField<>("loginField", new PropertyModel<>(this, "login"));
        this.loginField.setRequired(true);
        this.loginField.setLabel(JooqUtils.lookup("login", this));
        this.form.add(this.loginField);
        this.loginFeedback = new TextFeedbackPanel("loginFeedback", this.loginField);
        this.form.add(this.loginFeedback);

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setRequired(true);
        this.passwordField.setLabel(JooqUtils.lookup("password", this));
        this.form.add(this.passwordField);
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);
        this.form.add(this.passwordFeedback);

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"));
        this.retypePasswordField.setRequired(true);
        this.retypePasswordField.setLabel(JooqUtils.lookup("retypePassword", this));
        this.form.add(this.retypePasswordField);
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField);
        this.form.add(this.retypePasswordFeedback);

        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField));
        this.form.add(new UserLoginFormValidator(this.hostnameField, this.loginField));

        this.registerButton = new Button("registerButton");
        this.registerButton.setOnSubmit(this::registerButtonOnSubmit);
        this.form.add(this.registerButton);
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    private void registerButtonOnSubmit(Button button) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        DSLContext context = ApplicationUtils.getApplication().getDSLContext();
        HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
        HostnameRecord hostnameRecord = context.select(hostnameTable.fields()).from(hostnameTable).where(hostnameTable.FQDN.eq(hostname)).fetchOneInto(hostnameTable);

        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(hostnameRecord.getApplicationId())).fetchOneInto(applicationTable);

        JdbcTemplate applicationJdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(applicationRecord.getCode());
        String roleId = applicationJdbcTemplate.queryForObject("SELECT " + Jdbc.Role.ROLE_ID + " FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.NAME + " = ?", String.class, configuration.getString(Constants.ROLE_REGISTERED));

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
        user.put(Jdbc.User.FULL_NAME, this.login);
        user.put(Jdbc.User.PASSWORD, this.password);
        user.put(Jdbc.User.ROLE_ID, roleId);
        user.put(Jdbc.User.AUTHENTICATION, AuthenticationEnum.None.getLiteral());
        jdbcInsert.execute(user);
        applicationJdbcTemplate.update("UPDATE " + Jdbc.USER + " SET " + Jdbc.User.PASSWORD + " = MD5(?) WHERE " + Jdbc.User.USER_ID + " = ?", this.password, userId);
        setResponsePage(LoginPage.class);
    }

}
