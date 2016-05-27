package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/password/modify")
public class UserPasswordModifyPage extends MasterPage {

    private String applicationUserId;

    private String login;
    private Label loginLabel;

    private String password;
    private TextField<String> passwordField;
    private TextFeedbackPanel passwordFeedback;

    private String retypePassword;
    private TextField<String> retypePasswordField;
    private TextFeedbackPanel retypePasswordFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        PageParameters parameters = getPageParameters();
        this.applicationUserId = parameters.get("applicationUserId").toString();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        Map<String, Object> userRecord = null;
        userRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.USER + " WHERE " + Jdbc.User.USER_ID + " = ?", this.applicationUserId);

        this.login = (String) userRecord.get(Jdbc.User.LOGIN);
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"));
        this.form.add(this.loginLabel);

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setLabel(JooqUtils.lookup("password", this));
        this.form.add(this.passwordField);
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);
        this.form.add(this.passwordFeedback);

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"));
        this.retypePasswordField.setLabel(JooqUtils.lookup("retypePassword"));
        this.form.add(this.retypePasswordField);
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField);
        this.form.add(this.retypePasswordFeedback);

        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField));

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    @Override
    public String getPageHeader() {
        return "Modify User Password";
    }

    @Override
    public String getPageDescription() {
        return "reset a user password credential";
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        jdbcTemplate.update("UPDATE " + Jdbc.USER + " SET " + Jdbc.User.PASSWORD + " = MD5(?) WHERE " + Jdbc.User.USER_ID + " = ?", this.password, this.applicationUserId);
        setResponsePage(UserManagementPage.class);
    }

}
