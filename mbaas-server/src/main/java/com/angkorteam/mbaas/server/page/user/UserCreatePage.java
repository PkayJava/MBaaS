package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.jooq.enums.UserStatusEnum;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import com.angkorteam.mbaas.server.renderer.RoleChoiceRenderer;
import com.angkorteam.mbaas.server.validator.UserLoginValidator;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.UUID;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/create")
public class UserCreatePage extends Page {

    private String login;
    private TextField<String> loginField;
    private TextFeedbackPanel loginFeedback;

    private String password;
    private TextField<String> passwordField;
    private TextFeedbackPanel passwordFeedback;

    private String retypePassword;
    private TextField<String> retypePasswordField;
    private TextFeedbackPanel retypePasswordFeedback;

    private RolePojo role;
    private DropDownChoice<RolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();

        RoleTable roleTable = Tables.ROLE.as("roleTable");

        this.loginField = new TextField<String>("loginField", new PropertyModel<>(this, "login"));
        this.loginField.setRequired(true);
        this.loginField.add(new UserLoginValidator());
        this.loginField.setLabel(JooqUtils.lookup("login", this));
        this.loginFeedback = new TextFeedbackPanel("loginFeedback", this.loginField);


        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setLabel(JooqUtils.lookup("password", this));
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"));
        this.retypePasswordField.setLabel(JooqUtils.lookup("retypePassword"));
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField);

        List<RolePojo> roles = context.select(roleTable.fields()).from(roleTable).fetchInto(RolePojo.class);
        this.roleField = new DropDownChoice<>("roleField", new PropertyModel<>(this, "role"), roles, new RoleChoiceRenderer());
        this.roleField.setRequired(true);
        this.roleField.setLabel(JooqUtils.lookup("role", this));
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form = new Form<>("form");
        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField));
        add(this.form);

        this.form.add(this.loginField);
        this.form.add(this.loginFeedback);
        this.form.add(this.passwordField);
        this.form.add(this.passwordFeedback);
        this.form.add(this.retypePasswordField);
        this.form.add(this.retypePasswordFeedback);
        this.form.add(this.roleField);
        this.form.add(this.roleFeedback);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        String uuid = UUID.randomUUID().toString();
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.newRecord(userTable);
        userRecord.setUserId(uuid);
        userRecord.setDeleted(false);
        userRecord.setSystem(false);
        userRecord.setStatus(UserStatusEnum.Active.getLiteral());
        userRecord.setAccountNonExpired(true);
        userRecord.setAccountNonLocked(true);
        userRecord.setCredentialsNonExpired(true);
        userRecord.setLogin(this.login);
        userRecord.setPassword(this.password);
        userRecord.setRoleId(this.role.getRoleId());
        userRecord.store();

        context.update(userTable).set(userTable.PASSWORD, DSL.md5(password)).where(userTable.USER_ID.eq(uuid)).execute();

        setResponsePage(UserManagementPage.class);
    }

}
