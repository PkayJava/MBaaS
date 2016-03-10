package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
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
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/password/modify")
public class UserPasswordModifyPage extends MasterPage {

    private String userId;
    private Integer optimistic;

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
        DSLContext context = getDSLContext();

        UserTable userTable = Tables.USER.as("userTable");

        PageParameters parameters = getPageParameters();

        this.userId = parameters.get("userId").toString();

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(userId)).fetchOneInto(userTable);

        this.optimistic = userRecord.getOptimistic();

        this.login = userRecord.getLogin();
        this.loginLabel = new Label("loginLabel", new PropertyModel<>(this, "login"));

        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setLabel(JooqUtils.lookup("password", this));
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"));
        this.retypePasswordField.setLabel(JooqUtils.lookup("retypePassword"));
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form = new Form<>("form");
        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField));
        add(this.form);

        this.form.add(this.loginLabel);
        this.form.add(this.passwordField);
        this.form.add(this.passwordFeedback);
        this.form.add(this.retypePasswordField);
        this.form.add(this.retypePasswordFeedback);
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
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(userId)).fetchOneInto(userTable);
        userRecord.setOptimistic(this.optimistic);
        userRecord.setPassword(this.password);
        userRecord.update();
        setResponsePage(UserManagementPage.class);
    }

}
