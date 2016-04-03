package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.RoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.server.validator.UserLoginValidator;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.UUID;

/**
 * Created by socheat on 4/1/16.
 */
@Mount("/register")
public class RegisterPage extends AdminLTEPage {

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

        this.loginField = new TextField<>("loginField", new PropertyModel<>(this, "login"));
        this.loginField.setRequired(true);
        this.loginField.setLabel(JooqUtils.lookup("login", this));
        this.loginField.add(new UserLoginValidator());
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
        DSLContext context = getSession().getDSLContext();
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_REGISTERED))).fetchOneInto(roleTable);
        UserTable userTable = Tables.USER.as("userTable");
        String userId = UUID.randomUUID().toString();
        UserRecord userRecord = context.newRecord(userTable);
        userRecord.setUserId(userId);
        userRecord.setLogin(this.login);
        userRecord.setAuthentication(AuthenticationEnum.None.getLiteral());
        userRecord.setDeleted(false);
        userRecord.setAccountNonExpired(true);
        userRecord.setRoleId(roleRecord.getRoleId());
        userRecord.setAccountNonLocked(true);
        userRecord.setCredentialsNonExpired(true);
        userRecord.setPassword(this.password);
        userRecord.store();
        context.update(userTable).set(userTable.PASSWORD, DSL.md5(this.password)).where(userTable.USER_ID.eq(userId)).execute();
        setResponsePage(LoginPage.class);
    }

}
