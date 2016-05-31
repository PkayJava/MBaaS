package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MbaasRoleTable;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MbaasRolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasUserRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
import com.angkorteam.mbaas.server.renderer.MBaaSRoleChoiceRenderer;
import com.angkorteam.mbaas.server.validator.MBaaSUserLoginValidator;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
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
@AuthorizeInstantiation({"mbaas.system"})
@Mount("/mbaas/user/create")
public class UserCreatePage extends MBaaSPage {

    private String fullName;
    private TextField<String> fullNameField;
    private TextFeedbackPanel fullNameFeedback;

    private String login;
    private TextField<String> loginField;
    private TextFeedbackPanel loginFeedback;

    private String password;
    private TextField<String> passwordField;
    private TextFeedbackPanel passwordFeedback;

    private String retypePassword;
    private TextField<String> retypePasswordField;
    private TextFeedbackPanel retypePasswordFeedback;

    private MbaasRolePojo role;
    private DropDownChoice<MbaasRolePojo> roleField;
    private TextFeedbackPanel roleFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New User";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();

        this.form = new Form<>("form");
        add(this.form);

        MbaasRoleTable roleTable = Tables.MBAAS_ROLE.as("roleTable");

        this.fullNameField = new TextField<>("fullNameField", new PropertyModel<>(this, "fullName"));
        this.fullNameField.setRequired(true);
        this.fullNameField.setLabel(JooqUtils.lookup("fullName", this));
        this.form.add(fullNameField);
        this.fullNameFeedback = new TextFeedbackPanel("fullNameFeedback", this.fullNameField);
        this.form.add(fullNameFeedback);

        this.loginField = new TextField<>("loginField", new PropertyModel<>(this, "login"));
        this.loginField.setRequired(true);
        this.loginField.add(new MBaaSUserLoginValidator());
        this.loginField.setLabel(JooqUtils.lookup("login", this));
        this.form.add(loginField);
        this.loginFeedback = new TextFeedbackPanel("loginFeedback", this.loginField);
        this.form.add(loginFeedback);


        this.passwordField = new PasswordTextField("passwordField", new PropertyModel<>(this, "password"));
        this.passwordField.setLabel(JooqUtils.lookup("password", this));
        this.form.add(this.passwordField);
        this.passwordFeedback = new TextFeedbackPanel("passwordFeedback", this.passwordField);
        this.form.add(this.passwordFeedback);

        this.retypePasswordField = new PasswordTextField("retypePasswordField", new PropertyModel<>(this, "retypePassword"));
        this.retypePasswordField.setLabel(JooqUtils.lookup("retypePassword"));
        this.form.add(retypePasswordField);
        this.retypePasswordFeedback = new TextFeedbackPanel("retypePasswordFeedback", this.retypePasswordField);
        this.form.add(retypePasswordFeedback);

        this.form.add(new EqualPasswordInputValidator(this.passwordField, this.retypePasswordField));

        List<MbaasRolePojo> roles = context.select(roleTable.fields()).from(roleTable).fetchInto(MbaasRolePojo.class);
        this.roleField = new DropDownChoice<>("roleField", new PropertyModel<>(this, "role"), roles, new MBaaSRoleChoiceRenderer());
        this.roleField.setRequired(true);
        this.roleField.setLabel(JooqUtils.lookup("role", this));
        this.form.add(roleField);
        this.roleFeedback = new TextFeedbackPanel("roleFeedback", this.roleField);
        this.form.add(roleFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");
        MbaasUserRecord userRecord = context.newRecord(userTable);
        userRecord.setMbaasUserId(UUID.randomUUID().toString());
        userRecord.setFullName(this.fullName);
        userRecord.setSystem(false);
        userRecord.setAuthentication(AuthenticationEnum.None.getLiteral());
        userRecord.setStatus(UserStatusEnum.Active.getLiteral());
        userRecord.setAccountNonExpired(true);
        userRecord.setAccountNonLocked(true);
        userRecord.setCredentialsNonExpired(true);
        userRecord.setLogin(this.login);
        userRecord.setMbaasRoleId(this.role.getMbaasRoleId());
        userRecord.setPassword(this.password);
        userRecord.store();
        context.update(userTable).set(userTable.PASSWORD, DSL.md5(password)).where(userTable.MBAAS_USER_ID.eq(userRecord.getMbaasUserId())).execute();
        setResponsePage(UserManagementPage.class);
    }

}
